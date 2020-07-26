package p004cl.android.poc.pocrecarga;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.p000v4.app.DialogFragment;

/* renamed from: cl.android.poc.pocrecarga.MessageDialogFragment */
public class MessageDialogFragment extends DialogFragment {
    /* access modifiers changed from: private */
    public MessageDialogListener mListener;
    private String mMessage;
    private String mTitle;

    /* renamed from: cl.android.poc.pocrecarga.MessageDialogFragment$MessageDialogListener */
    public interface MessageDialogListener {
        void onDialogPositiveClick(DialogFragment dialogFragment);
    }

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setRetainInstance(true);
    }

    public static MessageDialogFragment newInstance(String title, String message, MessageDialogListener listener) {
        MessageDialogFragment fragment = new MessageDialogFragment();
        fragment.mTitle = title;
        fragment.mMessage = message;
        fragment.mListener = listener;
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setMessage(this.mMessage).setTitle(this.mTitle);
        builder.setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (MessageDialogFragment.this.mListener != null) {
                    MessageDialogFragment.this.mListener.onDialogPositiveClick(MessageDialogFragment.this);
                }
            }
        });
        return builder.create();
    }
}
