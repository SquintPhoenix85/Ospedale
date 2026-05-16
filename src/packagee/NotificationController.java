package packagee;

import javax.swing.JOptionPane;

public class NotificationController {

    public void show(Response<?> response) {
        if (response == null) {
            return;
        }

        int type;
        switch (response.getStatusCode()) {
            case OK -> type = JOptionPane.INFORMATION_MESSAGE;
            case BAD_REQUEST -> type = JOptionPane.WARNING_MESSAGE;
            case NOT_FOUND, UNAUTHORIZED -> type = JOptionPane.ERROR_MESSAGE;
            default -> type = JOptionPane.PLAIN_MESSAGE;
        }

        JOptionPane.showMessageDialog(null, response.getMessage(), response.getStatusCode().name(), type);
    }
}
