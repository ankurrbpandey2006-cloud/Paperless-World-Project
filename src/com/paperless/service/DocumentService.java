package com.paperless.service;

import com.paperless.dao.AuditLogDAO;
import com.paperless.dao.DocumentDAO;
import com.paperless.exception.ValidationException;
import com.paperless.model.AuditLog;
import com.paperless.model.Document;
import com.paperless.model.DocumentStatus;
import com.paperless.util.AsyncExecutor;
import com.paperless.util.Validator;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Document CRUD + audit logging (async) + collections analytics. */
public class DocumentService {
    private final DocumentDAO documentDAO = new DocumentDAO();
    private final AuditLogDAO auditDAO = new AuditLogDAO();

    public Document createDocument(String title, String content, int ownerId) throws ValidationException, SQLException {
        Validator.notBlank(title, "Title");
        Document d = new Document(title, content, ownerId);
        d.setStatus(DocumentStatus.PENDING_SIGNATURE);
        Document saved = documentDAO.save(d);
        asyncAudit("CREATE_DOCUMENT", ownerId, saved.getId(), "Created: " + title);
        return saved;
    }

    public List<Document> getAll() throws SQLException { return documentDAO.findAll(); }
    public List<Document> getByOwner(int ownerId) throws SQLException { return documentDAO.findByOwner(ownerId); }

    public boolean updateStatus(int docId, DocumentStatus status, int actorId) throws SQLException {
        var opt = documentDAO.findById(docId);
        if (opt.isEmpty()) return false;
        Document d = opt.get();
        d.setStatus(status);
        boolean ok = documentDAO.update(d);
        if (ok) asyncAudit("STATUS_CHANGE", actorId, docId, "Status -> " + status);
        return ok;
    }

    public boolean delete(int id) throws SQLException { return documentDAO.delete(id); }

    /** Dashboard: count per status */
    public Map<DocumentStatus, Long> statusCounts() throws SQLException {
        return documentDAO.findAll().stream().collect(Collectors.groupingBy(Document::getStatus, Collectors.counting()));
    }

    /** Generic sorted helper */
    public List<Document> sortedByTitle() throws SQLException {
        List<Document> list = documentDAO.findAll();
        list.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        return list;
    }

    private void asyncAudit(String action, int userId, int docId, String details) {
        AsyncExecutor.get().submit(() -> {
            try { auditDAO.save(new AuditLog(action, userId, docId, details)); }
            catch (SQLException e) { e.printStackTrace(); }
        });
    }
}
