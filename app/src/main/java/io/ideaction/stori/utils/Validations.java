package io.ideaction.stori.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {
   
   public static boolean isValidEmail(CharSequence target) {
      return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
   }
   
   public static boolean isValidPassword(final String password) {
      
      Pattern pattern;
      Matcher matcher;
      
      final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";
      
      pattern = Pattern.compile(PASSWORD_PATTERN);
      matcher = pattern.matcher(password);
      
      return matcher.matches();
      
   }
   
   public static boolean isPasswordMatching(String firstPass, String secondPass) {
      return firstPass.equals(secondPass);
   }
   
   public static void hideKeyboard(Activity activity) {
      if (activity.getCurrentFocus() != null) {
         InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
         imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
      }
   }
   
   public static void showKeyboard(Activity activity) {
      if (activity.getCurrentFocus() != null) {
         InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
         imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
      }
   }
}
