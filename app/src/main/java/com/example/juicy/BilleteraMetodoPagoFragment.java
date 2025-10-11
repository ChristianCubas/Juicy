package com.example.juicy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.juicy.databinding.FragmentBilleteraMetodoPagoBinding;

public class BilleteraMetodoPagoFragment extends Fragment {

    private enum SelectedCard {
        PRIMARY,
        SECONDARY
    }

    private FragmentBilleteraMetodoPagoBinding binding;
    private SelectedCard selectedCard = SelectedCard.PRIMARY;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBilleteraMetodoPagoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
        updateSelectionUI();
    }



    private void setupListeners() {
        binding.addPaymentButton.setOnClickListener(v -> NavHostFragment.findNavController(this)
                .navigate(R.id.addPaymentMethodFragment));

        binding.primaryCard.setOnClickListener(v -> selectCard(SelectedCard.PRIMARY));
        binding.secondaryCard.setOnClickListener(v -> selectCard(SelectedCard.SECONDARY));

        View.OnClickListener deleteListener = v -> new BorrarMetodoPagoFragment()
                .show(getParentFragmentManager(), BorrarMetodoPagoFragment.TAG);

        binding.primaryDeleteButton.setOnClickListener(deleteListener);
        binding.secondaryDeleteButton.setOnClickListener(deleteListener);
    }

    private void selectCard(@NonNull SelectedCard card) {
        if (selectedCard != card) {
            selectedCard = card;
            updateSelectionUI();
        }
    }

    private void updateSelectionUI() {
        if (binding == null) {
            return;
        }
        final int checkedIcon = R.drawable.ic_check_circle_file;
        final int uncheckedIcon = R.drawable.ic_uncheck_circle;

        boolean isPrimarySelected = selectedCard == SelectedCard.PRIMARY;
        binding.primaryCheckIcon.setImageResource(isPrimarySelected ? checkedIcon : uncheckedIcon);
        binding.secondaryCheckIcon.setImageResource(
                selectedCard == SelectedCard.SECONDARY ? checkedIcon : uncheckedIcon);

        int highlightedStroke = getResources().getDimensionPixelSize(R.dimen.payment_card_stroke);
        binding.primaryCard.setStrokeWidth(isPrimarySelected ? highlightedStroke : 0);
        binding.secondaryCard.setStrokeWidth(
                selectedCard == SelectedCard.SECONDARY ? highlightedStroke : 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}