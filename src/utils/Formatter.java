package utils;

/** Specifies how to convert an object to String */
public interface Formatter<T> {
  String getString(T e);
}
