
import JFlex.SilentExit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Clesio Nascimento
 */
public class ExecutarJFlex {

    public static void main(String[] args) {

        String lexerFile = System.getProperty("user.dir") + "/src/lexer.flex";
                //lexerColor = System.getProperty("user.dir") + "/src/lexerColor.flex";

        try {
            JFlex.Main.generate(new String[]{lexerFile/*, lexerColor*/});
        } catch (SilentExit ex) {
            Logger.getLogger(ExecutarJFlex.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
