package tanvir.firebaseauthentication;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText phoneET,pinET;
    String codeSent;
    //ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        phoneET=findViewById(R.id.phoneNumberET);
        pinET=findViewById(R.id.pinET);
        //progressDialog=new ProgressDialog(this);
    }

    public void sendVerificationCode(View view) {

        //progressDialog.showProgressDialog("Sending Pin Number...");
        String phoneNumber= phoneET.getText().toString();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      @Override
      public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
          Log.d("PhoneAuthCredential  ",phoneAuthCredential.toString());
          //progressDialog.hideProgressDialog();
        String code = phoneAuthCredential.getSmsCode();
          if (code != null) {
              pinET.setText(code);
              PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
              signInWithPhoneAuthCredential(credential);
          }
          else
              Log.d("PhoneAuthCredentialCode","null");
      }

      @Override
      public void onVerificationFailed(FirebaseException e) {
          Log.d("FirebaseException  ",e.toString());
          //progressDialog.hideProgressDialog();
      }

      @Override
      public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
          super.onCodeSent(s, forceResendingToken);
          //progressDialog.hideProgressDialog();
          Log.d("codeSent  ",s);
          codeSent=s;
      }
  };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressDialog.hideProgressDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Login Successfull", Toast.LENGTH_LONG).show();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        "Incorrect Verification Code ", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void varifySignInCode(View view) {
        String code = pinET.getText().toString();
        ///progressDialog.showProgressDialog("Varifying pin number...");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }
}
