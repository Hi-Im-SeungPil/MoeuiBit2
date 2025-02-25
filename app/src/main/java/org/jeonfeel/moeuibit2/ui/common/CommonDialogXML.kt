package org.jeonfeel.moeuibit2.ui.common

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.databinding.DialogCommonBinding

class CommonDialogXML(
    private val image: Int,
    private val message: String,
    private val leftButtonText: String = "취소",
    private val rightButtonText: String = "확인",
    private val onConfirm: () -> Unit,
    private val onCancel: () -> Unit,
) : DialogFragment() {

    private var _binding: DialogCommonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        _binding = DialogCommonBinding.inflate(requireActivity().layoutInflater)
        isCancelable = false

        binding.ivCommonDialogImage.setImageResource(image)

        binding.tvCommonDialogTitle.text = message

        binding.tvCommonDialogLeft.text = leftButtonText

        binding.tvCommonDialogRight.text = rightButtonText

        binding.tvCommonDialogLeft.setOnClickListener {
            onCancel()
            dismiss()
        }

        binding.tvCommonDialogRight.setOnClickListener {
            onConfirm()
            dismiss()
        }

        builder.setView(binding.root)
        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}