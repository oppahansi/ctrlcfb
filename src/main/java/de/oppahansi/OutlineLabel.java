package de.oppahansi;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class OutlineLabel extends JLabel {
  private Color outlineColor;
  private boolean isPaintingOutline;
  private boolean forceTransparent;
  private final int thickness;

  public OutlineLabel(String text, int horizontalAlignment, int thickness) {
    super(text, horizontalAlignment);
    this.outlineColor = Color.WHITE;
    this.isPaintingOutline = false;
    this.forceTransparent = false;
    this.thickness = thickness;
    this.setBorder(thickness);
  }

  private void setBorder(int thickness) {
    Border border = this.getBorder();
    Border margin = new EmptyBorder(thickness, thickness + 3, thickness, thickness + 3);
    this.setBorder(new CompoundBorder(border, margin));
  }

  public void setOutlineColor(Color outlineColor) {
    this.outlineColor = outlineColor;
    this.invalidate();
  }

  public Color getForeground() {
    return this.isPaintingOutline ? this.outlineColor : super.getForeground();
  }

  public boolean isOpaque() {
    return this.forceTransparent ? false : super.isOpaque();
  }

  public void paint(Graphics g) {
    String text = this.getText();
    if (text != null && !text.isEmpty()) {
      if (this.isOpaque()) {
        super.paint(g);
      }

      this.forceTransparent = true;
      this.isPaintingOutline = true;
      g.translate(-this.thickness, -this.thickness);
      super.paint(g);
      g.translate(this.thickness, 0);
      super.paint(g);
      g.translate(this.thickness, 0);
      super.paint(g);
      g.translate(0, this.thickness);
      super.paint(g);
      g.translate(0, this.thickness);
      super.paint(g);
      g.translate(-this.thickness, 0);
      super.paint(g);
      g.translate(-this.thickness, 0);
      super.paint(g);
      g.translate(0, -this.thickness);
      super.paint(g);
      g.translate(this.thickness, 0);
      this.isPaintingOutline = false;
      super.paint(g);
      this.forceTransparent = false;
    } else {
      super.paint(g);
    }
  }
}
