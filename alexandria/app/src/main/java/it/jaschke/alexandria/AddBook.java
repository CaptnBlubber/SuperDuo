package it.jaschke.alexandria;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatImageButton;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;
import rx.android.schedulers.AndroidSchedulers;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ISBN_13_START = "978";

    @Bind(R.id.txt_ean)
    EditText mTxtEan;
    @Bind(R.id.scan_button)
    Button mScanButton;
    @Bind(R.id.bookTitle)
    TextView mBookTitle;
    @Bind(R.id.bookSubTitle)
    TextView mBookSubTitle;
    @Bind(R.id.bookCover)
    ImageView mBookCover;
    @Bind(R.id.authors)
    TextView mAuthors;
    @Bind(R.id.categories)
    TextView mCategories;

    @Bind(R.id.delete_button)
    AppCompatImageButton mDeleteButton;
    @Bind(R.id.next_button)
    AppCompatImageButton mNextButton;

    private final int LOADER_ID = 1;
    private View mRootView;
    private final String EAN_CONTENT = "eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";
    private Snackbar mSnackbarNetworkError;


    public AddBook() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mTxtEan != null) {
            outState.putString(EAN_CONTENT, mTxtEan.getText().toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String scanned_ean = result.getContents();
            if (!TextUtils.isEmpty(scanned_ean)) {
                mTxtEan.setText(scanned_ean);
            }
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, mRootView);

        RxTextView
                .textChanges(mTxtEan)
                .map(charSequence -> {
                    clearFields();
                    return charSequence;
                })
                .filter(t -> (t.length() == 10 && !t.toString().startsWith(ISBN_13_START)) || t.length() == 13) // ISBN Numbers can only be 10 or 13 Characters Long. ISBN 10 should not start with 978
                .map(CharSequence::toString)
                .map(s -> {
                    if (s.length() == 10) { //Our filter already makes sure we do not get a false isbn10 number while typing
                        s = ISBN_13_START + s;
                    }
                    return s;
                })
                .filter(s -> s.startsWith(ISBN_13_START)) //make Sure all ISBN 13 numbers start with 978
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::performSearch);

        if (savedInstanceState != null) {
            mTxtEan.setText(savedInstanceState.getString(EAN_CONTENT));
        }


        return mRootView;
    }

    @OnClick(R.id.delete_button)
    public void deleteBook() {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, mTxtEan.getText().toString());
        bookIntent.setAction(BookService.DELETE_BOOK);
        getActivity().startService(bookIntent);
        mTxtEan.setText("");
    }

    @OnClick(R.id.next_button)
    public void clearInput() {
        mTxtEan.setText("");
    }

    @OnClick(R.id.scan_button)
    public void scanBarcode() {
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }


    public void performSearch(String ean) {

        //Once we have an ISBN, start a book intent
        if (!ConnectionUtils.isInternetConnectionAvailable(getContext())) {
            if (mSnackbarNetworkError != null && mSnackbarNetworkError.isShown()) {
                mSnackbarNetworkError.dismiss();
            }

            final String finalEan = ean;

            mSnackbarNetworkError = Snackbar.make(mRootView, R.string.no_connection_warning, Snackbar.LENGTH_LONG);
            mSnackbarNetworkError.setAction(R.string.action_retry, v -> {
                performSearch(finalEan);
            });
            mSnackbarNetworkError.show();

            return;
        }

        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.FETCH_BOOK);
        getActivity().startService(bookIntent);
        restartLoader();
    }


    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mTxtEan.getText().length() == 0) {
            return null;
        }

        String eanStr = mTxtEan.getText().toString();

        if (eanStr.length() == 10 && !eanStr.startsWith(ISBN_13_START)) {
            eanStr = ISBN_13_START + eanStr;
        }

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        mBookTitle.setText(bookTitle);

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        mBookSubTitle.setText(bookSubTitle);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String[] authorsArr = authors.split(",");

        mAuthors.setLines(authorsArr.length);
        mAuthors.setText(authors.replace(",", "\n"));

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if (Patterns.WEB_URL.matcher(imgUrl).matches()) {
            new DownloadImage(mBookCover).execute(imgUrl);
            mBookCover.setVisibility(View.VISIBLE);
        }

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        mCategories.setText(categories);

        mNextButton.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //Nothing to do
    }

    private void clearFields() {
        mBookTitle.setText("");
        mBookSubTitle.setText("");
        mAuthors.setText("");
        mCategories.setText("");

        mBookCover.setVisibility(View.INVISIBLE);
        mNextButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().setTitle(R.string.scan);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
