package com.example.juicy;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.juicy.databinding.FragmentBorrarMetodoPagoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BorrarMetodoPagoFragment extends BottomSheetDialogFragment {

    public static final String TAG = "DeletePaymentMethodBottomSheet";

    private FragmentBorrarMetodoPagoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBorrarMetodoPagoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.cancelButton.setOnClickListener(v -> dismiss());
        binding.confirmButton.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}