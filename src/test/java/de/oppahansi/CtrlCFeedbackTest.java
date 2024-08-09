package de.oppahansi;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CtrlCFeedbackTest {
  private CtrlCFeedback ctrlCFeedback;

  @BeforeEach
  public void setUp() {
    ctrlCFeedback = new CtrlCFeedback();
  }

  @Test
  public void testNativeHookRegistration() {
    assertDoesNotThrow(GlobalScreen::registerNativeHook);
  }

  @Test
  public void testNativeKeyPressed() {
    var mockEvent = mock(NativeKeyEvent.class);

    when(mockEvent.getKeyCode()).thenReturn(NativeKeyEvent.VC_C);
    when(mockEvent.getModifiers()).thenReturn(NativeKeyEvent.CTRL_MASK);

    ctrlCFeedback.nativeKeyPressed(mockEvent);

    var popupFrame = getPopupFrame(ctrlCFeedback);

    assertNotNull(popupFrame);
    assertTrue(popupFrame.isVisible());
  }

  @Test
  public void testNativeMouseMoved() {
    int expectedLocationX = 63;
    int expectedLocationY = 65;
    var mockEvent = mock(NativeMouseEvent.class);

    when(mockEvent.getX()).thenReturn(100);
    when(mockEvent.getY()).thenReturn(100);

    invokeShowPopupMessage(ctrlCFeedback);
    ctrlCFeedback.nativeMouseMoved(mockEvent);

    var popupFrame = getPopupFrame(ctrlCFeedback);

    assertNotNull(popupFrame);
    assertEquals(expectedLocationX, popupFrame.getLocation().x);
    assertEquals(expectedLocationY, popupFrame.getLocation().y);
  }

  @Test
  public void testShowPopupMessage() {
    invokeShowPopupMessage(ctrlCFeedback);

    var popupFrame = getPopupFrame(ctrlCFeedback);

    assertNotNull(popupFrame);
    assertTrue(popupFrame.isVisible());

    var closeTimer = getCloseTimer(ctrlCFeedback);

    assertNotNull(closeTimer);
    assertTrue(closeTimer.isRunning());
  }

  private void invokeShowPopupMessage(CtrlCFeedback ctrlCFeedback) {
    try {
      var method = CtrlCFeedback.class.getDeclaredMethod("showPopupMessage");

      method.setAccessible(true);
      method.invoke(ctrlCFeedback);
    } catch (Exception e) {
      fail("Failed to invoke showPopupMessage: " + e.getMessage());
    }
  }

  private JFrame getPopupFrame(CtrlCFeedback ctrlCFeedback) {
    try {
      var field = CtrlCFeedback.class.getDeclaredField("popupFrame");

      field.setAccessible(true);

      return (JFrame) field.get(ctrlCFeedback);
    } catch (Exception e) {
      fail("Failed to access popupFrame: " + e.getMessage());
      return null;
    }
  }

  private Timer getCloseTimer(CtrlCFeedback ctrlCFeedback) {
    try {
      var field = CtrlCFeedback.class.getDeclaredField("closeTimer");

      field.setAccessible(true);

      return (Timer) field.get(ctrlCFeedback);
    } catch (Exception e) {
      fail("Failed to access closeTimer: " + e.getMessage());
      return null;
    }
  }
}
