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
    // Claves para args/resultados
    public static final String ARG_ID     = "arg_id_metodo_pago";
    public static final String ARG_NUM    = "arg_num_mask";
    public static final String ARG_TIT    = "arg_titular";
    public static final String ARG_EXP    = "arg_exp";
    public static final String REQ_KEY    = "req_delete_payment_method";
    public static final String RES_ID     = "res_id_metodo_pago";

    private FragmentBorrarMetodoPagoBinding binding;

    public static BorrarMetodoPagoFragment newInstance(int id, String numMask, String titular, String exp) {
        BorrarMetodoPagoFragment f = new BorrarMetodoPagoFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_ID, id);
        b.putString(ARG_NUM, numMask);
        b.putString(ARG_TIT, titular);
        b.putString(ARG_EXP, exp);
        f.setArguments(b);
        return f;
    }

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

        // Pintar preview con los args
        Bundle args = getArguments() != null ? getArguments() : Bundle.EMPTY;
        String numMask = args.getString(ARG_NUM, "**** **** **** ****");
        String titular = args.getString(ARG_TIT, "");
        String exp     = args.getString(ARG_EXP, "");

        binding.deleteCardNumber.setText(numMask);
        binding.deleteCardHolder.setText(titular);
        binding.deleteCardMeta.setText("Vence " + exp);

        binding.cancelButton.setOnClickListener(v -> dismiss());

        binding.confirmButton.setOnClickListener(v -> {
            int id = args.getInt(ARG_ID, -1);
            Bundle result = new Bundle();
            result.putInt(RES_ID, id);
            // Devolver el id seleccionado al fragment padre
            getParentFragmentManager().setFragmentResult(REQ_KEY, result);
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
