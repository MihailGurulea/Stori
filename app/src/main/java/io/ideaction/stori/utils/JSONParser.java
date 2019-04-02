package io.ideaction.stori.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONParser {

    public static String registerJSON(String response) {
        String error = "Oops, something went wrong...";
        String title;
        StringBuilder content = new StringBuilder();

        try {
            JSONObject json = new JSONObject(response);

            if (json.has("message")) {
                title = json.getString("message");

                if (json.has("errors")) {
                    JSONObject jsonErrors = new JSONObject(String.valueOf(json.getJSONObject("errors")));

                    if (jsonErrors.has("email")) {
                        JSONArray emailsError = jsonErrors.getJSONArray("email");

                        for (int i = 0; i < emailsError.length(); i++) {
                            if (emailsError.get(i) != null) {
                                content.append(String.valueOf(emailsError.get(i))).append("\n");
                            }
                        }
                    }

                    if (jsonErrors.has("name")) {
                        JSONArray namesError = jsonErrors.getJSONArray("name");

                        for (int i = 0; i < namesError.length(); i++) {
                            if (namesError.get(i) != null) {
                                content.append(String.valueOf(namesError.get(i))).append("\n");
                            }
                        }
                    }

                    if (jsonErrors.has("password")) {
                        JSONArray passwordsError = jsonErrors.getJSONArray("password");

                        for (int i = 0; i < passwordsError.length(); i++) {
                            if (passwordsError.get(i) != null) {
                                content.append(String.valueOf(passwordsError.get(i))).append("\n");
                            }
                        }
                    }

                    if (jsonErrors.has("password_confirmation")) {
                        JSONArray passwordsConfirmationError = jsonErrors.getJSONArray("password_confirmation");

                        for (int i = 0; i < passwordsConfirmationError.length(); i++) {
                            if (passwordsConfirmationError.get(i) != null) {
                                content.append(String.valueOf(passwordsConfirmationError.get(i))).append("\n");
                            }
                        }
                    }

                    if (jsonErrors.has("native_lang")) {
                        JSONArray nativeLanguagesError = jsonErrors.getJSONArray("native_lang");

                        for (int i = 0; i < nativeLanguagesError.length(); i++) {
                            if (nativeLanguagesError.get(i) != null) {
                                content.append(String.valueOf(nativeLanguagesError.get(i))).append("\n");
                            }
                        }
                    }

                    if (jsonErrors.has("lang_to_learn")) {
                        JSONArray languagesToLearnError = jsonErrors.getJSONArray("lang_to_learn");

                        for (int i = 0; i < languagesToLearnError.length(); i++) {
                            if (languagesToLearnError.get(i) != null) {
                                content.append(String.valueOf(languagesToLearnError.get(i))).append("\n");
                            }
                        }
                    }

                    if (content.toString().length() > 1) {
                        return title + "\n" + content.toString().substring(0, content.toString().length() - 2);
                    } else {
                        return title;
                    }
                } else {
                    return title;
                }
            } else {
                return error;
            }
        } catch (Exception e) {
            return error;
        }
    }
}
