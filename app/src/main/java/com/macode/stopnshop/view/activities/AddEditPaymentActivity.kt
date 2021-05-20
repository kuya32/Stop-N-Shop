package com.macode.stopnshop.view.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.macode.stopnshop.R
import com.macode.stopnshop.databinding.ActivityAddEditPaymentBinding
import com.macode.stopnshop.model.Payment
import com.macode.stopnshop.utilities.Constants

class AddEditPaymentActivity : BaseActivity() {

    private var binding: ActivityAddEditPaymentBinding? = null
    private var monthSelected: String = ""
    private var yearSelected: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditPaymentBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        addMonthSpinner()
        addYearSpinner()

        if (intent.hasExtra(Constants.PAYMENT_DETAILS)) {
            paymentDetails = intent.getParcelableExtra(Constants.PAYMENT_DETAILS)!!
            establishPaymentInfo(paymentDetails)
        }

        setUpToolbar()

        binding!!.addEditPaymentCardNumberEditInput.addTextChangedListener(creditCardNumberFormattingTextWatcher)

        binding!!.addEditPaymentSubmitButton.setOnClickListener {
            validatePaymentMethodDetails()
        }
    }

    private fun establishPaymentInfo(paymentDetails: Payment) {
        binding!!.addEditPaymentNameEditInput.setText(paymentDetails.cardName)
        binding!!.addEditPaymentCardNumberEditInput.setText(paymentDetails.cardNumber)
        monthSelected = paymentDetails.expirationMonth
        binding!!.addEditMonthSpinner.setSelection(resources.getStringArray(R.array.numberedMonths).indexOf(monthSelected))
        yearSelected = paymentDetails.expirationYear
        binding!!.addEditYearSpinner.setSelection(resources.getStringArray(R.array.next20Years).indexOf(yearSelected))
        binding!!.addEditPaymentVerificationEditInput.setText(paymentDetails.verificationValue)
        when (paymentDetails.default) {
            "true" -> {
                binding!!.addEditDefaultCheckBox.isChecked = true
            }
            else -> {
                binding!!.addEditDefaultCheckBox.isChecked = false
            }
        }
    }

    private fun validatePaymentMethodDetails() {
        val cardName = binding!!.addEditPaymentNameEditInput.text.toString()
        val cardNumber = binding!!.addEditPaymentCardNumberEditInput.text.toString()
        monthSelected = binding!!.addEditMonthSpinner.selectedItem.toString()
        yearSelected = binding!!.addEditYearSpinner.selectedItem.toString()
        val cvv = binding!!.addEditPaymentVerificationEditInput.text.toString()
        val default = when {
            binding!!.addEditDefaultCheckBox.isChecked -> {
                true
            }
            else -> {
                false
            }
        }
        when {
            cardName.isEmpty() -> {
                showError(binding!!.addEditPaymentNameInput, "Please enter the full name on the card!")
            }
            cardNumber.isEmpty() || cardNumber.length != 20 -> {
                hideError(binding!!.addEditPaymentNameInput)
                showError(binding!!.addEditPaymentCardNumberInput, "Please enter a valid card number!")
            }
            monthSelected.isEmpty() -> {
                hideError(binding!!.addEditPaymentCardNumberInput)
                showErrorSnackBar("Please select the month the card expires!", true)
            }
            yearSelected.isEmpty() -> {
                hideError(binding!!.addEditPaymentCardNumberInput)
                showErrorSnackBar("Please select the year the card expires!", true)
            }
            cvv.isEmpty() || cvv.length != 3 -> {
                hideError(binding!!.addEditPaymentCardNumberInput)
                showError(binding!!.addEditPaymentVerificationInput, "Please enter the card verification value on the back of the card!")
            }
            else -> {
                hideError(binding!!.addEditPaymentVerificationInput)
                checkingForDefaultPayment(cardName, cardNumber, monthSelected, yearSelected, cvv, default)
            }
        }
    }

    private fun checkingForDefaultPayment(
        cardName: String,
        cardNumber: String,
        month: String,
        year: String,
        verificationValue: String,
        default: Boolean
    ) {
        if (default) {
            fireStoreClass.checkPaymentListItemsForDefault(this@AddEditPaymentActivity, cardName, cardNumber, month, year, verificationValue, default)
        } else {
            savingPaymentInfoToFirebase(cardName, cardNumber, month, year, verificationValue, default)
        }
    }

    private fun savingPaymentInfoToFirebase(
        cardName: String,
        cardNumber: String,
        month: String,
        year: String,
        verificationValue: String,
        default: Boolean
    ) {
        if (intent.hasExtra(Constants.PAYMENT_DETAILS)) {
            showProgressDialog("Updating credit card payment...")

            val payment = Payment(
                fireStoreClass.getCurrentUserID(),
                cardName,
                cardNumber,
                month,
                year,
                verificationValue,
                default.toString(),
                paymentDetails.id
            )
            fireStoreClass.updatePaymentMethod(this@AddEditPaymentActivity, payment, paymentDetails.id)
        } else {
            showProgressDialog("Saving payment method...")

            val payment = Payment(
                fireStoreClass.getCurrentUserID(),
                cardName,
                cardNumber,
                month,
                year,
                verificationValue,
                default.toString()
            )
            fireStoreClass.addPaymentMethod(this@AddEditPaymentActivity, payment)
        }
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.addEditPaymentToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        if (intent.hasExtra(Constants.PAYMENT_DETAILS)) {
            supportActionBar?.title = "Editing Credit Card Payment Method"
        } else {
            supportActionBar?.title = "Enter Your Credit Card Information"
        }
        toolbar.setTitleTextColor(Color.WHITE)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this@AddEditPaymentActivity, R.drawable.gradient_background))
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun addMonthSpinner() {
        val stateAdapter = ArrayAdapter<String>(this@AddEditPaymentActivity, R.layout.state_dropdown_item, resources.getStringArray(
            R.array.numberedMonths))
        binding!!.addEditMonthSpinner.adapter = stateAdapter

        binding!!.addEditMonthSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                monthSelected = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun addYearSpinner() {
        val stateAdapter = ArrayAdapter<String>(this@AddEditPaymentActivity, R.layout.state_dropdown_item, resources.getStringArray(
            R.array.next20Years))
        binding!!.addEditYearSpinner.adapter = stateAdapter

        binding!!.addEditYearSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                yearSelected = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

}