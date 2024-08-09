package de.oppahansi;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Window;

public class CtrlCFeedback implements NativeKeyListener, NativeMouseMotionListener {
  private JFrame popupFrame;
  private JLabel label;
  private Timer closeTimer;

  public CtrlCFeedback() {
    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      System.err.println(ex.getMessage());
      System.exit(1);
    }

    GlobalScreen.addNativeKeyListener(this);
    GlobalScreen.addNativeMouseMotionListener(this);
    var gcTimer =
        new Timer(
            60000,
            (e) -> {
              System.gc();
            });
    gcTimer.start();
  }

  @Override
  public void nativeKeyPressed(NativeKeyEvent e) {
    if (e.getKeyCode() == NativeKeyEvent.VC_C && this.isOnlyCtrlPressed(e)) {
      this.showPopupMessage();
    }
  }

  @Override
  public void nativeMouseMoved(NativeMouseEvent e) {
    if (popupFrame != null && popupFrame.isVisible()) {
      popupFrame.setLocation(
          e.getX() - popupFrame.getWidth() / 2, e.getY() - popupFrame.getHeight() - 10);
    }
  }

  private boolean isOnlyCtrlPressed(NativeKeyEvent e) {
    return e.getModifiers() == NativeInputEvent.CTRL_L_MASK
        || e.getModifiers() == NativeInputEvent.CTRL_MASK
        || e.getModifiers() == NativeInputEvent.CTRL_R_MASK
        || e.getModifiers() - NativeInputEvent.NUM_LOCK_MASK == NativeInputEvent.CTRL_L_MASK
        || e.getModifiers() - NativeInputEvent.NUM_LOCK_MASK == NativeInputEvent.CTRL_MASK
        || e.getModifiers() - NativeInputEvent.NUM_LOCK_MASK == NativeInputEvent.CTRL_R_MASK;
  }

  private void showPopupMessage() {
    if (label == null) {
      label = initOutlineLabel();
    }

    if (popupFrame == null) {
      popupFrame = initPopupFrame();
      popupFrame.add(label, "Center");
    }

    if (closeTimer != null && closeTimer.isRunning()) {
      closeTimer.stop();
    }

    var mouseLocation = MouseInfo.getPointerInfo().getLocation();
    popupFrame.setLocation(
        mouseLocation.x - popupFrame.getWidth() / 2, mouseLocation.y - popupFrame.getHeight() - 10);
    popupFrame.setVisible(true);

    if (closeTimer == null) {
        closeTimer =
            new Timer(
                1000,
                (e) -> {
                    popupFrame.setVisible(false);
                });
      closeTimer.setRepeats(false);
      closeTimer.start();
    } else {
      closeTimer.restart();
    }
  }

  private JFrame initPopupFrame() {
    var popupFrame = new JFrame();

    popupFrame.setUndecorated(true);
    popupFrame.setType(Window.Type.UTILITY);
    popupFrame.setLayout(new BorderLayout());
    popupFrame.setSize(new Dimension(75, 25));
    popupFrame.setAlwaysOnTop(true);
    popupFrame.setBackground(new Color(0, 0, 0, 0));

    return popupFrame;
  }

  private OutlineLabel initOutlineLabel() {
    var label = new OutlineLabel("Copied!", 0, 3);

    label.setForeground(Color.WHITE);
    label.setOpaque(false);
    label.setOutlineColor(Color.BLACK);

    return label;
  }
}
