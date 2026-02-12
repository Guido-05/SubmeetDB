import com.submeet.client.account.view.LoginView;
import com.submeet.client.account.controller.AuthControl;
import com.submeet.dbmsboundary.DBMSBoundary;


public class Main {
    public static void main(String[] args) {
        DBMSBoundary.startConnection();

        javax.swing.SwingUtilities.invokeLater(() -> {
            AuthControl authControl = new AuthControl();
            LoginView loginView = new LoginView(authControl);
            loginView.setVisible(true);
        });
    }
}
