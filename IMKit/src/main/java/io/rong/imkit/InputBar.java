package io.rong.imkit;

public class InputBar
{
  public static enum Type
  {
    TYPE_DEFAULT, TYPE_CS_ROBOT, TYPE_CS_HUMAN, TYPE_CS_ROBOT_FIRST, TYPE_CS_HUMAN_FIRST;
  }

  public static enum Style
  {
    STYLE_SWITCH_CONTAINER_EXTENSION, STYLE_SWITCH_CONTAINER, STYLE_CONTAINER_EXTENSION, STYLE_EXTENSION_CONTAINER, STYLE_CONTAINER;

    int v;

    public static Style getStyle(int v)
    {
      Style result = null;
      for (Style style : values())
        if (style.v == v) {
          result = style;
          break;
        }

      return result;
    }
  }
}