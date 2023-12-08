
import com.formdev.flatlaf.FlatIntelliJLaf;
import compilerTools.Directory;
import compilerTools.ErrorLSSL;
import compilerTools.Functions;
import compilerTools.Grammar;
import compilerTools.Production;
import compilerTools.TextColor;
import compilerTools.Token;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author Clesio Nascimento
 */
public class Compilador extends javax.swing.JFrame {

    private String title;
    private Directory directorio;
    private ArrayList<Token> tokens;
    private ArrayList<ErrorLSSL> errors;
    private ArrayList<TextColor> textsColor;
    private Timer timerKeyReleased;
    private ArrayList<Production> identProd;
    private HashMap<String, String> identificadores;
    private boolean codigoFoiCompilado = false;

    /**
     * Creates new form Compilador
     */
    public Compilador() {
        initComponents();
        init();
    }

    private void init() {
        title = "Compiler";
        setLocationRelativeTo(null);
        setTitle(title);
        directorio = new Directory(this, jtpCode, title, ".comp");
        addWindowListener(new WindowAdapter() {// Cuando presiona la "X" de la esquina superior derecha
            @Override
            public void windowClosing(WindowEvent e) {
                directorio.Exit();
                System.exit(0);
            }
        });
        Functions.setLineNumberOnJTextComponent(jtpCode);
        timerKeyReleased = new Timer(300, ((e) -> {
            timerKeyReleased.stop();
            colorAnalysis();
        }));
        Functions.insertAsteriskInName(this, jtpCode, () -> {
            timerKeyReleased.restart();
        });
        tokens = new ArrayList<>();
        errors = new ArrayList<>();
        textsColor = new ArrayList<>();
        identProd = new ArrayList<>();
        identificadores = new HashMap<>();
        Functions.setAutocompleterJTextComponent(new String[]{"cor", "numero", "leste", "oeste",
            "sul", "norte", "pintar"}, jtpCode, () -> {
            timerKeyReleased.restart();
        });
    }

    private void colorAnalysis() {
        /* Limpiar el arreglo de colores */
        textsColor.clear();
        /* Extraer rangos de colores */
        LexerColor lexerColor;
        try {
            File codigo = new File("color.encrypter");
            FileOutputStream output = new FileOutputStream(codigo);
            byte[] bytesText = jtpCode.getText().getBytes();
            output.write(bytesText);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(codigo), "UTF8"));
            lexerColor = new LexerColor(entrada);
            while (true) {
                Token textColor = lexerColor.yylex();
                if (textColor == null) {
                    break;
                }
               // textsColor.add(textColor);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        }
        Functions.colorTextPane(textsColor, jtpCode, new Color(40, 40, 40));
    }

    private void limparCampos() {
        Functions.clearDataInTable(tb1Tokens);
        jtaOutputConsole.setText("");
        tokens.clear();
        errors.clear();
        identProd.clear();
        identificadores.clear();
        codigoFoiCompilado = false;
    }

    private void compile() {
        limparCampos();
        analiseLexica(); //lexicalAnalysis
        preencherTokensTabela(); //fillTableTokens
        analiseSintatica(); //syntactiAnalysis
        analiseSemantica(); //semanticAnalysis
        printConsole();
        codigoFoiCompilado = true;

    }

    private void analiseLexica() {
        Lexer lexer;

        try {
            File codigo = new File("code.encrypter");
            FileOutputStream output = new FileOutputStream(codigo);
            byte[] bytesText = jtpCode.getText().getBytes();
            output.write(bytesText);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(codigo), "UTF-8"));
            lexer = new Lexer(entrada);
            while (true) {
                Token token = lexer.yylex();
                if (token == null) {
                    break;
                }
                tokens.add(token);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void preencherTokensTabela() {
        tokens.forEach(token -> {
            Object[] data = new Object[]{token.getLexicalComp(), token.getLexeme(),
                "[" + token.getLine() + ",  " + token.getColumn() + "]"};
            Functions.addRowDataInTable(tb1Tokens, data);
        });
    }

    private void analiseSintatica() {
        Grammar gramatica = new Grammar(tokens, errors);

        /*Eliminação dos erros*/
        gramatica.delete(new String[]{"ERROR", "ERROR_1", "ERROR_2"}, 1);
        
        /*Agrupação de valores*/
        gramatica.group("VALOR", "(Numero | ID)", true);
        
         /*Declaração de variáveis*/
        gramatica.group("Variavel", "Tipo_de_dado identificador Op_atribuicao VALOR", true);
        gramatica.group("Variavel", "Tipo_de_dado  Op_atribuicao VALOR", true, 
                   2, "Erro sintático: falta identificador da váriavel [#, %]");          
        
       
       
        
        
        
          
        
        
        gramatica.show();

    }

    private void analiseSemantica() {

    }

    private void printConsole() {
        int sizeErrors = errors.size();
        if (sizeErrors > 0) {
            Functions.sortErrorsByLineAndColumn(errors);
            String strErrors = "\n";
            for (ErrorLSSL error : errors) {
                String strError = String.valueOf(error);
                strErrors += strError + "\n";
            }
            jtaOutputConsole.setText("Compilação finalizada... \n" + strErrors + "\n a compilação terminou com erros");
        } else {
            jtaOutputConsole.setText("Compilação finalizada...");
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnNovo = new javax.swing.JButton();
        btnAbrir = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnSalvarComo = new javax.swing.JButton();
        btnCompilar = new javax.swing.JButton();
        btnExecutar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtpCode = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tb1Tokens = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtaOutputConsole = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnNovo.setText("Novo");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnAbrir.setText("Abrir");
        btnAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAbrirActionPerformed(evt);
            }
        });

        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnSalvarComo.setText("Guarda como");
        btnSalvarComo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarComoActionPerformed(evt);
            }
        });

        btnCompilar.setText("Compilar");
        btnCompilar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompilarActionPerformed(evt);
            }
        });

        btnExecutar.setText("Executar");
        btnExecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecutarActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jtpCode);

        tb1Tokens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Componente léxico", "Lexema", "[Linha, Coluna]"
            }
        ));
        jScrollPane2.setViewportView(tb1Tokens);

        jtaOutputConsole.setColumns(20);
        jtaOutputConsole.setRows(5);
        jScrollPane3.setViewportView(jtaOutputConsole);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnNovo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAbrir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalvar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSalvarComo)
                        .addGap(18, 18, 18)
                        .addComponent(btnCompilar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExecutar))
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovo)
                    .addComponent(btnAbrir)
                    .addComponent(btnSalvar)
                    .addComponent(btnSalvarComo)
                    .addComponent(btnCompilar)
                    .addComponent(btnExecutar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAbrirActionPerformed
        if (directorio.Open()) {
            colorAnalysis();
            limparCampos();
        }
    }//GEN-LAST:event_btnAbrirActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed

        if (directorio.Save()) {
            limparCampos();
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnSalvarComoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarComoActionPerformed
        if (directorio.SaveAs()) {
            limparCampos();
        }
    }//GEN-LAST:event_btnSalvarComoActionPerformed

    private void btnCompilarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompilarActionPerformed
        if (getTitle().contains("*") || getTitle().equals(title)) {
            if (directorio.Save()) {
                compile();
            }

        } else {
            compile();
        }

    }//GEN-LAST:event_btnCompilarActionPerformed

    private void btnExecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecutarActionPerformed
        btnCompilar.doClick();
        if (codigoFoiCompilado) {
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Não pode executar o  código, há um "
                        + "ou mais erros no código");
            } else {

            }
        }
    }//GEN-LAST:event_btnExecutarActionPerformed

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        directorio.New();
        limparCampos();
    }//GEN-LAST:event_btnNovoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new FlatIntelliJLaf());
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
                }
                new Compilador().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAbrir;
    private javax.swing.JButton btnCompilar;
    private javax.swing.JButton btnExecutar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSalvarComo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jtaOutputConsole;
    private javax.swing.JTextPane jtpCode;
    private javax.swing.JTable tb1Tokens;
    // End of variables declaration//GEN-END:variables
}
