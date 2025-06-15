package com.noddy.statussaver.views.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Switch
import androidx.recyclerview.widget.RecyclerView
import com.noddy.statussaver.R
import com.noddy.statussaver.databinding.ItemAutoCleanBinding
import com.noddy.statussaver.databinding.ItemSettingsBinding
import com.noddy.statussaver.models.SettingsModel
import com.noddy.statussaver.utils.AutoCleanScheduler
import com.noddy.statussaver.utils.SharedPrefKeys
import com.noddy.statussaver.utils.SharedPrefUtils

class SettingsAdapter(var list: ArrayList<SettingsModel>, var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_AUTO_CLEAN = 0
        private const val TYPE_REGULAR = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].title == "Auto Clean") TYPE_AUTO_CLEAN else TYPE_REGULAR
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_AUTO_CLEAN) {
            AutoCleanViewHolder(
                ItemAutoCleanBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
        } else {
            RegularViewHolder(
                ItemSettingsBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        when (holder) {
            is AutoCleanViewHolder -> holder.bind(model)
            is RegularViewHolder -> holder.bind(model, position)
        }
    }

    override fun getItemCount() = list.size

    inner class AutoCleanViewHolder(val binding: ItemAutoCleanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: SettingsModel) {
            binding.title.text = model.title
            binding.desc.text = model.desc

            val isAutoCleanEnabled = SharedPrefUtils.getPrefBoolean(SharedPrefKeys.PREF_AUTO_CLEAN, false)
            binding.switchAutoClean.isChecked = isAutoCleanEnabled

            binding.switchAutoClean.setOnCheckedChangeListener { _, isChecked ->
                SharedPrefUtils.putPrefBoolean(SharedPrefKeys.PREF_AUTO_CLEAN, isChecked)
                if (isChecked) {
                    AutoCleanScheduler.scheduleDailyCleanup(context)
                } else {
                    AutoCleanScheduler.cancelScheduledCleanup(context)
                }
            }
        }
    }

    inner class RegularViewHolder(val binding: ItemSettingsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: SettingsModel, position: Int) {
            binding.settingsTitle.text = model.title
            binding.settingsDesc.text = model.desc

            binding.root.setOnClickListener {
                when (position) {
                    0 -> showGuideDialog()
                    3 -> showDisclaimerDialog()
                    4 -> openPrivacyPolicy()
                    5 -> shareApp()
                    6 -> rateApp()
                }
            }
        }

        private fun showGuideDialog() {
            // Implementation
        }

        private fun showDisclaimerDialog() {
            AlertDialog.Builder(context)
                .setTitle("Disclaimer")
                .setMessage("This app is not affiliated with WhatsApp. Use it responsibly.")
                .setPositiveButton("OK", null)
                .show()
        }

        private fun openPrivacyPolicy() {
            // Open URL
        }

        private fun shareApp() {
            // Share intent
        }

        private fun rateApp() {
            // Open Play Store
        }
    }
}