package Modules;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public final class KeyBindsManager {
    Map<String, String> defaultKeyBindings = new HashMap<>(Map.of(
            "UP", "Up",
            "W", "Up",
            "DOWN", "Down",
            "S", "Down",
            "LEFT", "Left",
            "A", "Left",
            "RIGHT", "Right",
            "D", "Right"
    ));
    Map<String, Integer> keyActions = new HashMap<>(Map.of(
            "Up", 0,
            "Down", 0,
            "Left", 0,
            "Right", 0
    ));
    Map<String, Integer> keyFrames = new HashMap<>();

    private final InputMap inputMap;
    private final ActionMap actionMap;

    @SuppressWarnings("static-access")
    public KeyBindsManager(JComponent component) {
        this.inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        this.actionMap = component.getActionMap();
        setDefaultKeyBindings();
    }

    public void setDefaultKeyBindings() {
        for (Map.Entry<String, Integer> entry : keyActions.entrySet()) {
            addNewAction(entry.getKey());
        }
        for (Map.Entry<String, String> entry : defaultKeyBindings.entrySet()) {
            addKeyBinding(entry.getKey(), entry.getValue());
        }
    }

    // Sets up actions for key press and key release
    public void addNewAction(String actionName) {
        AbstractAction keyPressed =
                new AbstractAction() {
                    String action = actionName;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (keyActions.get(action) == 0) {
                            keyActions.replace(action, 1);
                        }
                    }
                };
        AbstractAction keyReleased =
                new AbstractAction() {
                    String action = actionName;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        keyActions.replace(action, 2);
                    }
                };
        keyFrames.put(actionName, 0);
        actionMap.put(actionName, keyPressed);
        actionMap.put(actionName + "Released", keyReleased);
    }

    public void addKeyBinding(String keyPressed, String action) {
        inputMap.put(KeyStroke.getKeyStroke(keyPressed), action);
        inputMap.put(KeyStroke.getKeyStroke("released " + keyPressed), action + "Released");
    }

    public void removeKeyBinding(String keyPressed) {
        inputMap.remove(KeyStroke.getKeyStroke(keyPressed));
    }

    public void changeKeyBinding(String oldKey, String newKey, String action) {
        removeKeyBinding(oldKey);
        addKeyBinding(newKey, action);
    }

    public Object[][] getActionInformation(){
        Object[][] ActionSet = new Object[keyActions.size()][];
        int actionNum = 0;
        for (Map.Entry<String,Integer> action : keyActions.entrySet()) {
            String actionName = action.getKey();
            ActionSet[actionNum] = new Object[]{actionName,action.getValue(),keyFrames.get(actionName)};
            actionNum++;
        }
        return ActionSet;
    }

    public void updateFrameInformation(){
        Object[][] Actions = getActionInformation();
        for (Object[] Action : Actions) {
            String actionName = (String)Action[0];
            Integer actionState = (Integer)Action[1];
            Integer actionFrameState = (Integer)Action[2];
            switch (actionState) {
                case 1 -> {
                    actionFrameState++;
                    keyFrames.replace(actionName, actionFrameState);
                }
                case 2 -> {
                    keyFrames.replace(actionName,0);
                    keyActions.replace(actionName,0);
                }
            }
        }
    }
}
