package com.example.calculadora;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.*;

import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView txtDisplay;
    private EditText edtResult;
    private String LastOperation;
    private final List<String> operations = new ArrayList<>(Arrays.asList("+", "-", "×", "÷"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtResult = findViewById(R.id.edtResult);
        txtDisplay = findViewById(R.id.txtDisplay);

        edtResult.setShowSoftInputOnFocus(false);

    }

    public void btnClick(View view) {
        Button btn = (Button) view;
        String btnText = btn.getText().toString();


        if (Character.isDigit(btnText.charAt(0))) {
            updateText(btnText);
            int cursorPos = edtResult.getSelectionStart();
            if (getString(R.string.zeroZero).equals(btnText))
                edtResult.setSelection(cursorPos + 1);
        } else if (isAnOperation(btnText)) {
            LastOperation = btnText;
            inputOperation(btnText);
        } else if (btnText.equals(".")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inputDot(btnText);
            }
        }
    }

    private boolean isAnOperation(String value) {
        if (operations.contains(value)) {
            return true;
        }
        return false;
    }

    private boolean isLastCharacterAnOperation(String value) {
        for (String operation : operations) {
            if (value.endsWith(operation))
                return true;
        }
        return false;
    }

    private void inputOperation(String strToAdd) {

        String oldStr = edtResult.getText().toString();
        int cursorPos = edtResult.getSelectionStart();
        boolean canUpdate = true;
        if (oldStr.length() != 0 && cursorPos != 0) {

            if(cursorPos < oldStr.length()){
                canUpdate = !operations.contains(String.valueOf(oldStr.charAt(cursorPos)));
            }

            String valueInCursorPos = String.valueOf(oldStr.charAt(cursorPos - 1));

            if (!isAnOperation(valueInCursorPos) && !valueInCursorPos.equals("(") && !valueInCursorPos.equals(".") && canUpdate) {
                updateText(strToAdd);
            } else if (valueInCursorPos.equals("(") || valueInCursorPos.equals(".") && canUpdate) {
                updateText("0");
                updateText(strToAdd);
            } else {
                if(!canUpdate)
                    cursorPos++;
                char operation = oldStr.charAt(cursorPos - 1);
                oldStr = oldStr.replace(operation, strToAdd.charAt(0));
                delete(cursorPos, oldStr.length());
                updateText(strToAdd);
            }
        } else {
            updateText("0");
            updateText(strToAdd);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void inputDot(String strToAdd) {
        String oldStr = edtResult.getText().toString();
        if (oldStr.length() != 0) {
            int cursorPos = edtResult.getSelectionStart();
            String leftString = "";
            if(cursorPos != 0)
                leftString = oldStr.substring(0, cursorPos);

            if (leftString.endsWith("(") || isAnOperation(leftString) || isLastCharacterAnOperation(leftString)) {
                updateText("0");
                updateText(strToAdd);
            } else if (!oldStr.contains(".")) {
                updateText(strToAdd);
            } else {
                String verify = getUpdatedText(strToAdd);
                verify = verify.replace("+", " ").replace("-", " ").replace("×", " ").replace("÷", " ");
                List<String> expressions = new ArrayList<>(Arrays.asList(verify.split(" ")));
                boolean canUpdate = true;
                for (String value : expressions) {
                    int count;
                    String temp = value.replace('.', ' ');
                    temp = temp.replaceAll("\\s", "");
                    count = value.length() - temp.length();
                    if (count > 1)
                        canUpdate = false;
                }
                if (canUpdate)
                    updateText(strToAdd);
            }
        } else {
            updateText("0");
            updateText(strToAdd);
        }
    }

    private String getUpdatedText(String strToAdd) {
        String oldStr = edtResult.getText().toString();
        int cursorPos = edtResult.getSelectionStart();
        String leftString = "";
        if(cursorPos != 0)
            leftString = oldStr.substring(0, cursorPos);
        String rightString = oldStr.substring(cursorPos);
        return String.format("%s%s%s", leftString, strToAdd, rightString);
    }

    private void updateText(String strToAdd) {
        int cursorPos = edtResult.getSelectionStart();

        edtResult.setText(getUpdatedText(strToAdd));
        edtResult.setSelection(cursorPos + 1);
    }

    public void btnParentheses(View view) {
        int openPar = 0;
        int closedPar = 0;
        int cursorPos = edtResult.getSelectionStart();
        int textLen = edtResult.getText().length();

        for (int i = 0; i < cursorPos; i++) {
            if (edtResult.getText().toString().charAt(i) == '(') {
                openPar += 1;
            }
            if (edtResult.getText().toString().charAt(i) == ')') {
                closedPar += 1;
            }
        }

        if (openPar == closedPar || edtResult.getText().toString().charAt(textLen - 1) == '(') {
            updateText("(");
        } else if (closedPar < openPar && edtResult.getText().toString().charAt(textLen - 1) != '(') {
            if (isLastCharacterAnOperation(edtResult.getText().toString()) || edtResult.getText().toString().endsWith("."))
                updateText("0");
            updateText(")");
        }

        edtResult.setSelection(cursorPos + 1);
    }

    public void btnClearAll(View view) {
        txtDisplay.setText("");
        edtResult.setText("");
    }

    public void btnBackspace(View view) {
        int cursorPos = edtResult.getSelectionStart();
        int textLen = edtResult.getText().length();
        delete(cursorPos, textLen);
    }

    private void delete(int cursorPos, int textLen) {

        if (textLen != 0 && cursorPos != 0) {
            SpannableStringBuilder selection = (SpannableStringBuilder) edtResult.getText();
            selection.replace(cursorPos - 1, cursorPos, "");

            edtResult.setText(selection);
            edtResult.setSelection(cursorPos - 1);
        }
    }

    public void btnEquals(View view) {

        String userExpression = edtResult.getText().toString();
        if (isLastCharacterAnOperation(userExpression))
            delete(edtResult.getSelectionStart(), edtResult.getText().length());

        userExpression = userExpression.replaceAll("÷", "/");
        userExpression = userExpression.replaceAll("×", "*");

        Expression exp = new Expression(userExpression);
        String result = String.valueOf(exp.calculate());

        if (!edtResult.getText().toString().equals(result))
            txtDisplay.setText(edtResult.getText());

        edtResult.setText(result);
        edtResult.setSelection(result.length());

        if (edtResult.getText().toString().equals("NaN"))
            edtResult.setText("");
    }
}