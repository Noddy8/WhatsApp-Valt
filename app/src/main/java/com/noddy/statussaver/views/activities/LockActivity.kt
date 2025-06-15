package com.noddy.statussaver.views.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.andrognito.pinlockview.PinLockListener
import com.noddy.statussaver.databinding.ActivityLockBinding
import com.noddy.statussaver.utils.SharedPrefKeys
import com.noddy.statussaver.utils.SharedPrefUtils
import java.util.concurrent.Executor

class LockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLockBinding
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isAppLockEnabled()) {
            unlockApp()
            return
        }

        setupBiometricAuth()
        setupPinLock()
    }

    private fun isAppLockEnabled(): Boolean {
        return SharedPrefUtils.getPrefBoolean(SharedPrefKeys.PREF_APP_LOCK_ENABLED, false)
    }

    private fun setupBiometricAuth() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(
            this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    unlockApp()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    binding.pinLockView.visibility = View.VISIBLE
                    binding.indicatorDots.visibility = View.VISIBLE
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Status Saver")
            .setSubtitle("Use your fingerprint or face")
            .setNegativeButtonText("Use PIN")
            .build()

        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            binding.pinLockView.visibility = View.VISIBLE
            binding.indicatorDots.visibility = View.VISIBLE
        }
    }

    private fun setupPinLock() {
        binding.pinLockView.setPinLockListener(object : PinLockListener {
            override fun onComplete(pin: String) {
                val savedPin = SharedPrefUtils.getPrefString(SharedPrefKeys.PREF_APP_LOCK_PIN, "")
                if (pin == savedPin) {
                    unlockApp()
                } else {
                    binding.pinLockView.resetPinLockView()
                    binding.textError.visibility = View.VISIBLE
                }
            }

            override fun onEmpty() {}
            override fun onPinChange(pinLength: Int, intermediatePin: String) {}
        })

        binding.textUseBiometric.setOnClickListener {
            try {
                biometricPrompt.authenticate(promptInfo)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun unlockApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}