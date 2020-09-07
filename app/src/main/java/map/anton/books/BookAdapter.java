package map.anton.books;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookAdapter extends  RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private final List<Book> books;

    public BookAdapter(List<Book> books){this.books=books;}

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_cardview, parent, false);

        return new BookViewHolder(v,books);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book currentBook = books.get(position);
        holder.mPages.setText(Integer.toString(currentBook.getNrPages()));
        holder.mTitle.setText(currentBook.getTitle());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }


    static class BookViewHolder extends RecyclerView.ViewHolder{
        final TextView mTitle;
        final TextView mPages;
         BookViewHolder(@NonNull View v, final List<Book> books){
            super(v);
            mTitle = v.findViewById(R.id.titleText);
            mPages = v.findViewById(R.id.pageNr);
        }
    }


}