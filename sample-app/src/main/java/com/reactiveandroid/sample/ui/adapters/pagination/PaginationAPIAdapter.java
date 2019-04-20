package com.reactiveandroid.sample.ui.adapters.pagination;

//pull request - bendothall
//Pagination API PaginationAPIAdapter

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.reactiveandroid.sample.R;
import com.reactiveandroid.sample.mvp.models.Note;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;

public class PaginationAPIAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Note>
            Notes;

    private Context
            context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;
    // View Types
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private PaginationAPIAdapterCallback
            mCallback;

    private String
            errorMsg;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy H:mm");

    public PaginationAPIAdapter(Context context) {
        this.context = context;
        this.mCallback = (PaginationAPIAdapterCallback) context;
        setHasStableIds(true);
        Notes = new ArrayList<>();
    }

    public List<Note> getNotes() {
        return Notes;
    }

    public void setNotes(List<Note> Notes) {
        this.Notes = Notes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case ITEM:
                View viewItem = inflater.inflate(R.layout.pagination_api_adapter_view, parent, false);
                viewHolder = new ItemVH(viewItem);
                break;

            case LOADING:
                View viewLoading = inflater.inflate(R.layout.pagination_api_adapter_progress_view, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Note result = Notes.get(position);

        switch (getItemViewType(position)) {

            case ITEM:

                final ItemVH itemVH = (ItemVH)holder;
                if(result.getId() != null){

                    itemVH.itemView.setTag(itemVH.getAdapterPosition());

                    itemVH.position.setText("" + (position + 1));
                    itemVH.id.setText(result.getId().toString());

                    TextDrawable noteDrawable = TextDrawable.builder().buildRound(result.getTitle().substring(0, 1), result.getColor());
                    itemVH.note_drawable.setImageDrawable(noteDrawable);

                    itemVH.title.setText(result.getTitle());
                    itemVH.text.setText(result.getText());

                    itemVH.note_updated_time.setText(DATE_FORMAT.format(result.getUpdatedAt()));

                }

                break;

        case LOADING:

                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return Notes == null ? 0 : Notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == Notes.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(Note note) {
        Notes.add(note);
        notifyItemInserted(Notes.size() - 1);
    }

    public void addAll(List<Note> notes) {
        for (Note result : notes) {
            add(result);
        }
    }

    public void remove(Note note) {
        int position = Notes.indexOf(note);
        if (position > -1) {
            Notes.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Note());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = Notes.size() - 1;
        Note result = getItem(position);

        if (result != null) {
            Notes.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public Note getItem(int position) {
        return Notes.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(Notes.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    protected class ItemVH extends RecyclerView.ViewHolder {

        public TextView
                position,
                id,
                title,
                text,
                note_updated_time;

        public ImageView
                note_drawable;

        public ItemVH(final View itemView) {
            super(itemView);

            position =  (TextView) itemView.findViewById(R.id.position);
            id	     =  (TextView) itemView.findViewById(R.id.id);
            note_drawable	 =  (ImageView) itemView.findViewById(R.id.note_drawable);
            title	 =  (TextView) itemView.findViewById(R.id.title);
            text	 =  (TextView) itemView.findViewById(R.id.text);
            note_updated_time = (TextView) itemView.findViewById(R.id.note_updated_time);

        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            //mRetryBtn.setOnClickListener(this);
            //mErrorLayout.setOnClickListener(this);


            mRetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

            mErrorLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                }
            });


        }


    }

}