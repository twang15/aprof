/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RoutinesFilterDialog.java
 *
 * Created on 29-apr-2010, 17.21.25
 */

package aprofplot.gui;

/**
 *
 * @author bruno
 */
public class RoutinesFilterDialog extends javax.swing.JDialog {

    /** Creates new form RoutinesFilterDialog */
    public RoutinesFilterDialog(java.awt.Frame parent, boolean modal, java.util.ArrayList<String> v, String[] criteria) {
        super(parent, modal);
        liblist = v;
        initComponents();
        setResizable(false);
        setSize(260, 350);
        setLocationRelativeTo(parent);
        if (criteria[0] != null) {
            jCheckBox1.setSelected(true);
            jTextField1.setEnabled(true);
            jTextField1.setText(criteria[0]);
        }
        if (criteria[1] != null) {
            jCheckBox2.setSelected(true);
            jComboBox1.setEnabled(true);
            jComboBox1.setSelectedItem(criteria[1]);
        }
        if (criteria[2] != null) {
            jCheckBox3.setSelected(true);
            jTextField2.setEnabled(true);
            jTextField2.setText(criteria[2]);
        }
        if (criteria[3] != null) {
            jCheckBox4.setSelected(true);
            jTextField3.setEnabled(true);
            jTextField3.setText(criteria[3]);
        }
        if (criteria[4] != null) {
            jCheckBox5.setSelected(true);
            jTextField4.setEnabled(true);
            jTextField4.setText(criteria[4]);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jCheckBox2 = new javax.swing.JCheckBox();
        jComboBox1 = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jCheckBox3 = new javax.swing.JCheckBox();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jCheckBox4 = new javax.swing.JCheckBox();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jCheckBox5 = new javax.swing.JCheckBox();
        jTextField4 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Filter routines");
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/aprofplot/gui/resources/Filter-icon.png"))).getImage());
        setModal(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Criteria"));
        jPanel1.setLayout(new java.awt.GridLayout(5, 1, 0, 4));

        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        jCheckBox1.setText("Time %  >=  ");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        jPanel3.add(jCheckBox1);

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextField1.setEnabled(false);
        jTextField1.setMaximumSize(new java.awt.Dimension(100, 27));
        jTextField1.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField1.setPreferredSize(new java.awt.Dimension(100, 27));
        jPanel3.add(jTextField1);

        jLabel2.setText(" %");
        jPanel3.add(jLabel2);

        jPanel1.add(jPanel3);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jCheckBox2.setText("Library: ");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });
        jPanel4.add(jCheckBox2);

        jComboBox1.setBackground(java.awt.Color.white);
        jComboBox1.setModel(new RoutinesFilterComboBoxModel(liblist));
        jComboBox1.setEnabled(false);
        jComboBox1.setMaximumSize(new java.awt.Dimension(150, 27));
        jComboBox1.setMinimumSize(new java.awt.Dimension(150, 27));
        jComboBox1.setPreferredSize(new java.awt.Dimension(150, 27));
        jPanel4.add(jComboBox1);

        jPanel1.add(jPanel4);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jCheckBox3.setText("Calls %  >=  ");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });
        jPanel5.add(jCheckBox3);

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextField2.setEnabled(false);
        jTextField2.setMaximumSize(new java.awt.Dimension(100, 27));
        jTextField2.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField2.setPreferredSize(new java.awt.Dimension(100, 27));
        jPanel5.add(jTextField2);

        jLabel3.setText(" %");
        jPanel5.add(jLabel3);

        jPanel1.add(jPanel5);

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        jCheckBox4.setText("Avg ratio >=");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });
        jPanel6.add(jCheckBox4);

        jTextField3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextField3.setEnabled(false);
        jTextField3.setMaximumSize(new java.awt.Dimension(100, 27));
        jTextField3.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField3.setPreferredSize(new java.awt.Dimension(100, 27));
        jPanel6.add(jTextField3);

        jLabel4.setText(" μs");
        jPanel6.add(jLabel4);

        jPanel1.add(jPanel6);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        jCheckBox5.setText("#Sms  >=    ");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });
        jPanel7.add(jCheckBox5);

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        jTextField4.setEnabled(false);
        jTextField4.setMaximumSize(new java.awt.Dimension(100, 27));
        jTextField4.setMinimumSize(new java.awt.Dimension(100, 27));
        jTextField4.setPreferredSize(new java.awt.Dimension(100, 27));
        jPanel7.add(jTextField4);

        jPanel1.add(jPanel7);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jButton1.setText("    Ok    ");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(javax.swing.Box.createHorizontalGlue());
        jPanel2.add(jButton1);

        jButton2.setText(" Cancel ");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
        if (jCheckBox1.isSelected()) jTextField1.setEnabled(true);
        else jTextField1.setEnabled(false);
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        // TODO add your handling code here:
        if (jCheckBox3.isSelected()) jTextField2.setEnabled(true);
        else jTextField2.setEnabled(false);
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
        // TODO add your handling code here:
        if (jCheckBox4.isSelected()) jTextField3.setEnabled(true);
        else jTextField3.setEnabled(false);
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
        if (jCheckBox2.isSelected()) jComboBox1.setEnabled(true);
        else jComboBox1.setEnabled(false);
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if (validateUserInput()) {
            this.dispose();
            String[] criteria = new String[5];
            if (jCheckBox1.isSelected()) criteria[0] = jTextField1.getText();
            if (jCheckBox2.isSelected()) criteria[1] = (String)jComboBox1.getSelectedItem();
            if (jCheckBox3.isSelected()) criteria[2] = jTextField2.getText();
            if (jCheckBox4.isSelected()) criteria[3] = jTextField3.getText();
            if (jCheckBox5.isSelected()) criteria[4] = jTextField4.getText();
            ((MainWindow)this.getParent()).setRoutinesTableFilter(criteria);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed
        // TODO add your handling code here:
        if (jCheckBox5.isSelected()) jTextField4.setEnabled(true);
        else jTextField4.setEnabled(false);
    }//GEN-LAST:event_jCheckBox5ActionPerformed

   private boolean validateUserInput() {
       if (jCheckBox1.isSelected()) {
           try{
               double time_perc = Double.parseDouble(jTextField1.getText());
               if (time_perc < 0 || time_perc > 100) throw (new Exception());
           }
           catch (Exception e) {
               javax.swing.JOptionPane.showMessageDialog(this,
                                                         "Time % must be a positive decimal\nwithin the range 0-100",
                                                         "Invalid input",
                                                         javax.swing.JOptionPane.ERROR_MESSAGE);
               return false;
           }
       }
       if (jCheckBox3.isSelected()) {
           try{
               double calls_perc = Double.parseDouble(jTextField2.getText());
               if (calls_perc < 0 || calls_perc > 100) throw (new Exception());
           }
           catch (Exception e) {
               javax.swing.JOptionPane.showMessageDialog(this,
                                                         "Calls % must be a positive decimal\nwithin the range 0-100",
                                                         "Invalid input",
                                                         javax.swing.JOptionPane.ERROR_MESSAGE);
               return false;
           }
       }
       if (jCheckBox4.isSelected()) {
           try{
               double avg_ratio = Double.parseDouble(jTextField3.getText());
               if (avg_ratio < 0) throw (new Exception());
           }
           catch (Exception e) {
               javax.swing.JOptionPane.showMessageDialog(this,
                                                         "Avg ratio must be a positive decimal",
                                                         "Invalid input",
                                                         javax.swing.JOptionPane.ERROR_MESSAGE);
               return false;
           }

       }
       if (jCheckBox5.isSelected()) {
           try{
               double n_sms = Double.parseDouble(jTextField4.getText());
               if (n_sms < 0 || n_sms != Math.ceil(n_sms)) throw (new Exception());
           }
           catch (Exception e) {
               javax.swing.JOptionPane.showMessageDialog(this,
                                                         "#Sms must be a positive integer",
                                                         "Invalid input",
                                                         javax.swing.JOptionPane.ERROR_MESSAGE);
               return false;
           }

       }
       return true;
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
    private java.util.ArrayList<String> liblist;
}
