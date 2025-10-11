package com.example.juicy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.juicy.databinding.FragmentMpagoBinding;

public class MpagoFragment extends Fragment {

    private enum SelectedMethod {
        SAVED_PRIMARY,
        SAVED_SECONDARY,
        OTHER_VISA,
        OTHER_PAYPAL
    }

    private FragmentMpagoBinding binding;
    private SelectedMethod selectedMethod = SelectedMethod.SAVED_PRIMARY;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMpagoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        updateSelectionUI();
    }

    private void setupListeners() {
        binding.primaryCard.setOnClickListener(v -> selectMethod(SelectedMethod.SAVED_PRIMARY));
        binding.secondaryCard.setOnClickListener(v -> selectMethod(SelectedMethod.SAVED_SECONDARY));
        binding.otherVisaCard.setOnClickListener(v -> selectMethod(SelectedMethod.OTHER_VISA));
        binding.otherPaypalCard.setOnClickListener(v -> selectMethod(SelectedMethod.OTHER_PAYPAL));
    }

    private void selectMethod(@NonNull SelectedMethod method) {
        if (selectedMethod != method) {
            selectedMethod = method;
            updateSelectionUI();
        }
    }

    private void updateSelectionUI() {
        if (binding == null) {
            return;
        }
        int checkedIcon = R.drawable.ic_check_circle_file;
        int uncheckedIcon = R.drawable.ic_uncheck_circle;

        binding.primaryCardStatus.setImageResource(
                selectedMethod == SelectedMethod.SAVED_PRIMARY ? checkedIcon : uncheckedIcon);
        binding.secondaryCardStatus.setImageResource(
                selectedMethod == SelectedMethod.SAVED_SECONDARY ? checkedIcon : uncheckedIcon);
        binding.otherVisaStatus.setImageResource(
                selectedMethod == SelectedMethod.OTHER_VISA ? checkedIcon : uncheckedIcon);
        binding.otherPaypalStatus.setImageResource(
                selectedMethod == SelectedMethod.OTHER_PAYPAL ? checkedIcon : uncheckedIcon);

        binding.otherVisaFields.setVisibility(
                selectedMethod == SelectedMethod.OTHER_VISA ? View.VISIBLE : View.GONE);
        binding.otherPaypalFields.setVisibility(
                selectedMethod == SelectedMethod.OTHER_PAYPAL ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}