package Controller.Validation;

import javafx.scene.control.Label;

public class LabelErrorStrategy implements ErrorStrategy {
    private final Label labelError;

    public LabelErrorStrategy(Label labelError) {
        labelError.setVisible(false);
        this.labelError = labelError;
    }

    @Override
    public void handleError(String message) {
        labelError.setVisible(true);
        labelError.setText(message);
    }
}

