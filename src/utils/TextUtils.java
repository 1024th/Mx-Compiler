package utils;

import java.util.ArrayList;

public class TextUtils {
  /** java equivalent of python code 
   * {@code sep.join([fmt.getString(x) for x in arr])}
   */
  public static <T> String join(ArrayList<T> arr, String sep, Formatter<T> fmt) {
    var it = arr.iterator();
    if (!it.hasNext())
      return "";
    StringBuilder sb = new StringBuilder();
    while (true) {
      T e = it.next();
      sb.append(fmt.getString(e));
      if (!it.hasNext())
        return sb.toString();
      sb.append(sep);
    }
  }

  /** java equivalent of python code 
   * {@code ", ".join([fmt.getString(x) for x in arr])}
   */
  public static <T> String join(ArrayList<T> arr, Formatter<T> fmt) {
    return join(arr, ", ", fmt);
  }

  /** java equivalent of python code 
   * {@code ", ".join([x.toString() for x in arr])}
   */
  public static <T> String join(ArrayList<T> arr) {
    return join(arr, ", ", x -> x.toString());
  }
}
