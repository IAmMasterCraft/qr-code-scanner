package com.mastercraft.qrcodescanner;

import android.Manifest;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.OnDialogButtonClickListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QRFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QRFragment extends Fragment implements ZXingScannerView.ResultHandler {

    private ZXingScannerView qrScanner;
    private SweetAlertDialog alertDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public QRFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QRFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QRFragment newInstance(String param1, String param2) {
        QRFragment fragment = new QRFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getContext())
                        .withTitle("Permission Denied")
                        .withMessage("This is needed to scan QR Code!")
                        .withButtonText("Ok", new OnDialogButtonClickListener() {
                            @Override
                            public void onClick() {
                                Navigation.findNavController(getActivity(), R.id.fragment_holder).navigate(R.id.action_QRFragment_to_homeFragment);
                            }
                        })
                        .withIcon(R.drawable.qr_icon_alt)
                        .build();
        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(dialogPermissionListener)
                .check();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_q_r, container, false);
        qrScanner = new ZXingScannerView(getActivity());
        return qrScanner;
    }

    private void notificationSound () {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        qrScanner.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        qrScanner.setResultHandler(this);
        qrScanner.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        notificationSound();
        qrScanner.stopCamera();
        alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
        alertDialog
                .setTitleText("QR Content")
                .setContentText(rawResult.getText())
                .setConfirmButtonBackgroundColor(R.color.main)
                .setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        alertDialog.dismissWithAnimation();
                        Navigation.findNavController(getActivity(), R.id.fragment_holder).navigate(R.id.action_QRFragment_to_homeFragment);
                    }
                })
                .setCancelable(false);
        alertDialog.show();
    }
}