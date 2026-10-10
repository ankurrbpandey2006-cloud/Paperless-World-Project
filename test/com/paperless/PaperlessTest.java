package com.paperless;

import com.paperless.model.*;
import com.paperless.service.*;
import com.paperless.util.DBUtil;
import com.paperless.util.PasswordUtil;
import com.paperless.util.Validator;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class PaperlessTest {

    @Before
    public void initDb() throws Exception {
        DBUtil.initDatabase();
    }

    // --- Utils ---
    @Test
    public void testPasswordHashAndVerify() {
        String hash = PasswordUtil.hash("hello123");
        assertTrue(PasswordUtil.verify("hello123", hash));
        assertFalse(PasswordUtil.verify("wrong", hash));
    }

    @Test(expected = com.paperless.exception.ValidationException.class)
    public void testValidatorRejectsInvalidEmail() throws Exception {
        Validator.email("not-an-email");
    }

    @Test
    public void testValidatorAcceptsValidEmail() throws Exception {
        Validator.email("test@example.com");
    }

    // --- OOP: polymorphism via Auditable ---
    @Test
    public void testPolymorphismAuditable() {
        List<Auditable> list = new ArrayList<>();
        list.add(new User("A", "a@a.com", "h", Role.SIGNER));
        list.add(new Document("Doc1", "content", 1));
        for (Auditable a : list) {
            assertNotNull(a.getDisplaySummary());
            assertNotNull(a.auditInfo());
        }
    }

    // --- Generics + Collections: filterByRole ---
    @Test
    public void testFilterByRoleGenerics() throws Exception {
        UserService us = new UserService();
        List<User> all = us.getAllUsers();
        List<User> signers = us.filterByRole(all, Role.SIGNER);
        for (User u : signers) assertEquals(Role.SIGNER, u.getRole());
    }

    @Test
    public void testGroupByRole() throws Exception {
        UserService us = new UserService();
        Map<Role, List<User>> grouped = us.groupByRole();
        assertFalse(grouped.isEmpty());
    }

    @Test
    public void testCountByRole() throws Exception {
        UserService us = new UserService();
        Map<Role, Long> counts = us.countByRole();
        assertTrue(counts.values().stream().mapToLong(Long::longValue).sum() >= 3);
    }

    // --- AuthService: registration + login ---
    @Test
    public void testRegisterAndLogin() throws Exception {
        AuthService auth = new AuthService();
        String email = "test_" + System.currentTimeMillis() + "@example.com";
        User u = auth.register("Test User", email, "password123", Role.SIGNER);
        assertNotNull(u);
        assertTrue(u.getId() > 0);
        User logged = auth.login(email, "password123");
        assertEquals(email, logged.getEmail());
    }

    @Test(expected = com.paperless.exception.AuthenticationException.class)
    public void testLoginWrongPasswordFails() throws Exception {
        AuthService auth = new AuthService();
        auth.login("admin@paperless.com", "wrongpass");
    }

    // --- DocumentService: CRUD ---
    @Test
    public void testCreateAndFetchDocument() throws Exception {
        DocumentService ds = new DocumentService();
        // use admin user id 1
        Document d = ds.createDocument("TestDoc-" + System.currentTimeMillis(), "Hello content", 1);
        assertTrue(d.getId() > 0);
        assertEquals(DocumentStatus.PENDING_SIGNATURE, d.getStatus());
        // allow async audit to complete
        Thread.sleep(300);
        List<Document> all = ds.getAll();
        assertTrue(all.stream().anyMatch(x -> x.getId() == d.getId()));
    }

    @Test
    public void testStatusCounts() throws Exception {
        DocumentService ds = new DocumentService();
        Map<DocumentStatus, Long> counts = ds.statusCounts();
        assertNotNull(counts);
    }

    // --- SignatureService: signing is synchronized ---
    @Test
    public void testSignatureTemplateAndSign() throws Exception {
        SignatureService ss = new SignatureService();
        DocumentService ds = new DocumentService();
        Document d = ds.createDocument("SignTest-" + System.currentTimeMillis(), "sign me", 2);
        Thread.sleep(200);
        var tmpl = ss.createTemplate("MySig-" + System.currentTimeMillis(), "Alice ✍️", 2);
        assertTrue(tmpl.getId() > 0);
        var sig = ss.signDocument(d.getId(), 2, tmpl.getId(), "127.0.0.1");
        assertNotNull(sig);
        assertTrue(sig.getId() > 0);
        Thread.sleep(300);
    }

    // --- ReviewService ---
    @Test
    public void testReviewAndCompliance() throws Exception {
        DocumentService ds = new DocumentService();
        SignatureService ss = new SignatureService();
        ReviewService rs = new ReviewService();
        Document d = ds.createDocument("ReviewTest-" + System.currentTimeMillis(), "review me", 2);
        Thread.sleep(200);
        var tmpl = ss.createTemplate("RevSig-" + System.currentTimeMillis(), "sig", 2);
        ss.signDocument(d.getId(), 2, tmpl.getId(), "127.0.0.1");
        Thread.sleep(300);
        var review = rs.submitReview(d.getId(), 3, "Looks good", ComplianceStatus.COMPLIANT, true);
        assertNotNull(review);
        assertTrue(review.isApproved());
        Map<ComplianceStatus, Long> counts = rs.complianceCounts();
        assertTrue(counts.getOrDefault(ComplianceStatus.COMPLIANT, 0L) >= 1);
    }

    // --- Inheritance: BaseEntity ---
    @Test
    public void testBaseEntityInheritance() {
        User u = new User("X", "x@x.com", "h", Role.REVIEWER);
        assertNotNull(u.getCreatedAt());
        assertNotNull(u.getUpdatedAt());
        u.touch();
        assertNotNull(u.getUpdatedAt());
    }

    // --- Multithreading: concurrent document creation ---
    @Test
    public void testConcurrentDocumentCreation() throws Exception {
        DocumentService ds = new DocumentService();
        int threads = 5;
        List<Thread> workers = new ArrayList<>();
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < threads; i++) {
            int idx = i;
            Thread t = new Thread(() -> {
                try { ds.createDocument("Concurrent-" + idx + "-" + System.nanoTime(), "c", 1); }
                catch (Exception e) { errors.add(e); }
            });
            workers.add(t); t.start();
        }
        for (Thread t : workers) t.join();
        assertTrue("Errors in concurrent creation: " + errors, errors.isEmpty());
        Thread.sleep(500);
    }
}
