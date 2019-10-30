package net.dirox.idareyou.domain.account.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseUser
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.identity.TwitterLoginButton
import kotlinx.android.synthetic.main.activity_twitter.*
import net.dirox.idareyou.R
import net.dirox.idareyou.domain.base.BaseActivity

class TwitterLoginActivity : BaseActivity(), View.OnClickListener {

    // [START declare_auth]
    //private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    private var loginButton: TwitterLoginButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure Twitter SDK
        val authConfig = TwitterAuthConfig(
            getString(R.string.twitter_consumer_key),
            getString(R.string.twitter_consumer_secret))
        )

        val twitterConfig = TwitterConfig.Builder(this)
            .twitterAuthConfig(authConfig)
            .build()

        Twitter.initialize(twitterConfig)

        // Inflate layout (must be done after Twitter is configured)
        setContentView(R.layout.activity_twitter)

        loginButton = buttonTwitterLogin

        buttonTwitterSignout.setOnClickListener(this)

        // [START initialize_auth]
        // Initialize Firebase Auth
        // auth = FirebaseAuth.getInstance()
        // [END initialize_auth]

        // [START initialize_twitter_login]

        buttonTwitterLogin?.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                Log.d(TAG, "twitterLogin:success$result")
                handleTwitterSession(result.data)
            }

            override fun failure(exception: TwitterException) {
                Log.w(TAG, "twitterLogin:failure", exception)
                updateUI(null)
            }
        }
        // [END initialize_twitter_login]
    }

    // [START on_start_check_user]
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        /*val currentUser = auth.currentUser
        updateUI(currentUser)*/
    }
    // [END on_start_check_user]

    // [START on_activity_result]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result to the Twitter login button.
        loginButton!!.onActivityResult(requestCode, resultCode, data)
    }
    // [END on_activity_result]

    // [START auth_with_twitter]
    private fun handleTwitterSession(session: TwitterSession) {
        Log.d(TAG, "handleTwitterSession:$session")
        // [START_EXCLUDE silent]
        showProgressLoading()
        // [END_EXCLUDE]


        Log.e("data", session.authToken.token)
        Log.e("data", session.authToken.secret)

        /* auth.signInWithCredential(credential)
             .addOnCompleteListener(this) { task ->
                 if (task.isSuccessful) {
                     // Sign in success, update UI with the signed-in user's information
                     Log.d(TAG, "signInWithCredential:success")
                     val user = auth.currentUser
                     updateUI(user)
                 } else {
                     // If sign in fails, display a message to the user.
                     Log.w(TAG, "signInWithCredential:failure", task.exception)
                     Toast.makeText(
                         baseContext, "Authentication failed.",
                         Toast.LENGTH_SHORT
                     ).show()
                     updateUI(null)
                 }

                 // [START_EXCLUDE]
                 hideProgressLoading()
                 // [END_EXCLUDE]
             }*/
    }
    // [END auth_with_twitter]

    private fun signOut() {
        //auth.signOut()
        TwitterCore.getInstance().sessionManager.clearActiveSession()

        updateUI(null)
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressLoading()
        if (user != null) {
            status.text = getString(R.string.twitter_status_fmt, user.displayName)
            detail.text = getString(R.string.firebase_status_fmt, user.uid)

            buttonTwitterLogin.visibility = View.GONE
            buttonTwitterSignout.visibility = View.VISIBLE
        } else {
            status.setText("sign out")
            detail.text = null

            buttonTwitterLogin.visibility = View.VISIBLE
            buttonTwitterSignout.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.buttonTwitterSignout) {
            signOut()
        }
    }

    companion object {
        private const val TAG = "TwitterLogin"
    }
}
