package com.paperless.ui;

import com.paperless.dao.AuditLogDAO;
import com.paperless.model.*;
import com.paperless.service.DocumentService;
import com.paperless.service.ReviewService;
import com.paperless.service.SignatureService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReviewerDashboard extends JFrame {
    private final User currentUser;
    private final DocumentService docService = new DocumentService();
    private final SignatureService sigService = new SignatureService();
    private final ReviewService reviewService = new ReviewService();
    private final AuditLogDAO auditDAO = new AuditLogDAO();

    private final DefaultTableModel docModel = new DefaultTableModel(new Object[]{"ID","Title","Status","Compliance","Owner"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable docTable = new JTable(docModel);

    private final DefaultTableModel reviewModel = new DefaultTableModel(new Object[]{"ID","Doc","Reviewer","Compliance","Approved","Comments"},0){
        @Override public boolean isCellEditable(int r,int c){return false;}
    };
    private final JTable reviewTable = new JTable(reviewModel);

    public ReviewerDashboard(User user) {
        super("Reviewer — Paperless World  |  " + user.getName());
        this.currentUser = user;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIStyle.BG);
        setLayout(new BorderLayout());
        add(header(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UIStyle.BODY);
        tabs.addTab("Document Review", reviewPanel());
        tabs.addTab("Compliance Check", compliancePanel());
        add(tabs, BorderLayout.CENTER);
        loadDocs(); loadReviews();
    }

    private JPanel header(){
        JPanel p=new JPanel(new BorderLayout());
        p.setBackground(new Color(0x4C_1D_95));
        p.setBorder(new EmptyBorder(12,18,12,18));
        JLabel t=new JLabel("Reviewer Dashboard");
        t.setFont(new Font("Segoe UI",Font.BOLD,16)); t.setForeground(Color.WHITE);
        JLabel u=new JLabel(currentUser.getEmail()+"  •  "+currentUser.getRole().getDisplayName());
        u.setForeground(new Color(0xDD_D6_FE));
        JButton lo=UIStyle.outlineButton("Logout");
        lo.setBackground(Color.WHITE);
        lo.addActionListener(e->{dispose(); new LoginFrame().setVisible(true);});
        p.add(t,BorderLayout.WEST); p.add(u,BorderLayout.CENTER); p.add(lo,BorderLayout.EAST);
        return p;
    }

    private JPanel reviewPanel(){
        JPanel p=new JPanel(new BorderLayout(10,10));
        p.setBackground(UIStyle.BG); p.setBorder(new EmptyBorder(12,12,12,12));
        UIStyle.styleTable(docTable); UIStyle.styleTable(reviewTable);
        JSplitPane split=new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(docTable), new JScrollPane(reviewTable));
        split.setResizeWeight(0.5);
        p.add(split, BorderLayout.CENTER);
        // label
        JPanel top=new JPanel(new BorderLayout());
        top.setBackground(UIStyle.BG);
        JLabel l1=new JLabel("Signed documents & signature logs  —  select a document and click Review");
        l1.setFont(UIStyle.H2); top.add(l1, BorderLayout.WEST);
        p.add(top, BorderLayout.NORTH);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(UIStyle.BG);
        JButton viewLog=UIStyle.outlineButton("View Signature Logs");
        JButton viewAudit=UIStyle.outlineButton("View Audit Logs");
        JButton reviewBtn=UIStyle.primaryButton("Review Selected");
        JButton refresh=UIStyle.outlineButton("Refresh");
        actions.add(viewLog); actions.add(viewAudit); actions.add(reviewBtn); actions.add(refresh);
        p.add(actions, BorderLayout.SOUTH);
        viewLog.addActionListener(e->showSigLogs());
        viewAudit.addActionListener(e->showAuditForSelected());
        reviewBtn.addActionListener(e->showReviewDialog());
        refresh.addActionListener(e->{loadDocs(); loadReviews();});
        return p;
    }

    private JPanel compliancePanel(){
        JPanel p=new JPanel(new BorderLayout(10,10));
        p.setBackground(UIStyle.BG); p.setBorder(new EmptyBorder(12,12,12,12));
        UIStyle.styleTable(reviewTable);
        // reuse review table for compliance view; add summary on top
        JLabel summary=new JLabel();
        summary.setFont(UIStyle.BODY);
        p.add(summary, BorderLayout.NORTH);
        p.add(new JScrollPane(reviewTable), BorderLayout.CENTER);
        JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(UIStyle.BG);
        JButton refresh=UIStyle.primaryButton("Refresh Report");
        actions.add(refresh); p.add(actions, BorderLayout.SOUTH);
        refresh.addActionListener(e->{loadReviews(); updateSummary(summary);});
        updateSummary(summary);
        return p;
    }

    private void updateSummary(JLabel label){
        try{
            var map=reviewService.complianceCounts();
            long comp=map.getOrDefault(ComplianceStatus.COMPLIANT,0L);
            long non=map.getOrDefault(ComplianceStatus.NON_COMPLIANT,0L);
            long pend=map.getOrDefault(ComplianceStatus.PENDING,0L);
            label.setText(String.format("<html><b>Compliance status:</b>  COMPLIANT: %d &nbsp; | &nbsp; NON_COMPLIANT: %d &nbsp; | &nbsp; PENDING: %d &nbsp;&nbsp; —  Review reports below</html>", comp, non, pend));
        }catch(Exception ex){ label.setText("Compliance summary unavailable: "+ex.getMessage()); }
    }

    private void loadDocs(){
        try{
            List<Document> docs=docService.getAll();
            docModel.setRowCount(0);
            for(Document d: docs) docModel.addRow(new Object[]{d.getId(), d.getTitle(), d.getStatus(), d.getComplianceStatus(), d.getOwnerId()});
        }catch(Exception ex){err(ex);}
    }

    private void loadReviews(){
        try{
            var list=reviewService.getAll();
            reviewModel.setRowCount(0);
            for(Review r: list) reviewModel.addRow(new Object[]{r.getId(), r.getDocumentId(), r.getReviewerId(), r.getComplianceStatus(), r.isApproved()?"YES":"NO", r.getComments()});
        }catch(Exception ex){err(ex);}
    }

    private void showSigLogs(){
        int r=docTable.getSelectedRow();
        if(r<0){JOptionPane.showMessageDialog(this,"Select a document first"); return;}
        int docId=(int)docModel.getValueAt(r,0);
        try{
            var sigs=sigService.getSignaturesForDocument(docId);
            var audits=auditDAO.findByDocument(docId);
            StringBuilder sb=new StringBuilder();
            sb.append("Signatures for doc ").append(docId).append(":\n");
            for(var s: sigs) sb.append("  ").append(s.getDisplaySummary()).append("\n");
            sb.append("\nAudit logs:\n");
            for(var a: audits) sb.append("  ").append(a.getDisplaySummary()).append(" — ").append(a.getDetails()).append("\n");
            if(sigs.isEmpty() && audits.isEmpty()) sb.append("  (no logs yet)");
            JTextArea area=new JTextArea(sb.toString()); area.setEditable(false); area.setFont(new Font("Consolas",Font.PLAIN,12));
            JScrollPane sp=new JScrollPane(area); sp.setPreferredSize(new Dimension(700,350));
            JOptionPane.showMessageDialog(this, sp, "Signature Logs — Doc #"+docId, JOptionPane.PLAIN_MESSAGE);
        }catch(Exception ex){err(ex);}
    }

    private void showAuditForSelected(){
        int r=docTable.getSelectedRow();
        if(r<0){JOptionPane.showMessageDialog(this,"Select a document first"); return;}
        int docId=(int)docModel.getValueAt(r,0);
        try{
            var audits=auditDAO.findByDocument(docId);
            DefaultTableModel m=new DefaultTableModel(new Object[]{"ID","Action","User","Details","Time"},0);
            for(var a: audits) m.addRow(new Object[]{a.getId(), a.getAction(), a.getUserId(), a.getDetails(), a.getTimestamp()});
            JTable t=new JTable(m); UIStyle.styleTable(t);
            JScrollPane sp=new JScrollPane(t); sp.setPreferredSize(new Dimension(750,300));
            JOptionPane.showMessageDialog(this, sp, "Audit — Doc #"+docId, JOptionPane.PLAIN_MESSAGE);
        }catch(Exception ex){err(ex);}
    }

    private void showReviewDialog(){
        int r=docTable.getSelectedRow();
        if(r<0){JOptionPane.showMessageDialog(this,"Select a document to review"); return;}
        int docId=(int)docModel.getValueAt(r,0);
        JTextArea commentsA=new JTextArea(4,30);
        JComboBox<ComplianceStatus> compBox=new JComboBox<>(ComplianceStatus.values());
        JCheckBox approvedBox=new JCheckBox("Approved", true);
        JPanel form=new JPanel(new GridLayout(0,1,6,6));
        form.add(new JLabel("Comments")); form.add(new JScrollPane(commentsA));
        form.add(new JLabel("Compliance status")); form.add(compBox);
        form.add(approvedBox);
        int res=JOptionPane.showConfirmDialog(this, form, "Review — Doc #"+docId, JOptionPane.OK_CANCEL_OPTION);
        if(res!=JOptionPane.OK_OPTION) return;
        try{
            ComplianceStatus cs=(ComplianceStatus)compBox.getSelectedItem();
            boolean approved=approvedBox.isSelected();
            reviewService.submitReview(docId, currentUser.getId(), commentsA.getText(), cs, approved);
            JOptionPane.showMessageDialog(this, "Review report submitted — Compliance: "+cs+"  Approved: "+approved);
            loadDocs(); loadReviews();
        }catch(Exception ex){err(ex);}
    }

    private void err(Exception ex){ JOptionPane.showMessageDialog(this, ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
}
