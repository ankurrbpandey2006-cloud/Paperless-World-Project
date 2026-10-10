package com.paperless.service;

import com.paperless.dao.AuditLogDAO;
import com.paperless.dao.DocumentDAO;
import com.paperless.dao.SignatureDAO;
import com.paperless.dao.SignatureTemplateDAO;
import com.paperless.exception.ValidationException;
import com.paperless.model.*;
import com.paperless.util.AsyncExecutor;
import com.paperless.util.PasswordUtil;
import com.paperless.util.Validator;

import java.sql.SQLException;
import java.util.List;

/**
 * Document Signing + Signature Management.
 * Includes synchronized signing to demonstrate thread-safety.
 */
public class SignatureService {
    private final SignatureDAO signatureDAO = new SignatureDAO();
    private final SignatureTemplateDAO templateDAO = new SignatureTemplateDAO();
    private final DocumentDAO documentDAO = new DocumentDAO();
    private final AuditLogDAO auditDAO = new AuditLogDAO();

    public SignatureTemplate createTemplate(String name, String data, int userId) throws ValidationException, SQLException {
        Validator.notBlank(name, "Template name");
        Validator.notBlank(data, "Signature data");
        SignatureTemplate t = new SignatureTemplate(name, data, userId);
        return templateDAO.save(t);
    }

    public List<SignatureTemplate> getTemplatesByUser(int userId) throws SQLException {
        return templateDAO.findByUser(userId);
    }

    public List<SignatureTemplate> getAllTemplates() throws SQLException { return templateDAO.findAll(); }

    public boolean deleteTemplate(int id) throws SQLException { return templateDAO.delete(id); }

    /** Thread-safe signing. */
    public synchronized Signature signDocument(int documentId, int signerId, int templateId, String ip)
            throws ValidationException, SQLException {
        var docOpt = documentDAO.findById(documentId);
        if (docOpt.isEmpty()) throw new ValidationException("Document not found");
        Document doc = docOpt.get();
        if (doc.getStatus() == DocumentStatus.SIGNED) throw new ValidationException("Document already signed");

        String hash = PasswordUtil.hash(documentId + ":" + signerId + ":" + System.currentTimeMillis());
        Signature sig = new Signature(documentId, signerId, templateId, hash);
        sig.setIpAddress(ip);
        Signature saved = signatureDAO.save(sig);

        doc.setStatus(DocumentStatus.SIGNED);
        documentDAO.update(doc);

        AsyncExecutor.get().submit(() -> {
            try { auditDAO.save(new AuditLog("SIGN_DOCUMENT", signerId, documentId, "Signed with template " + templateId)); }
            catch (SQLException e) { e.printStackTrace(); }
        });
        return saved;
    }

    public List<Signature> getSignaturesForDocument(int docId) throws SQLException {
        return signatureDAO.findByDocument(docId);
    }

    public List<Signature> getAllSignatures() throws SQLException { return signatureDAO.findAll(); }
}
