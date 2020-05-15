package com.example.android.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.example.android.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.DetailViewHolder> {

    private static final int VIEW_TYPE_MOVIE = 0;
    private static final int VIEW_TYPE_TRAILER = 1;
    private static final int VIEW_TYPE_REVIEW = 2;
    private static final String APPEND_USER_RATING = "/10";


    private List<Trailer> trailers;
    private List<Review> reviews;
    private DetailAdapterOnClickHandler clickHandler;
    private Movie movie;
    private boolean isFavorite;

    public interface DetailAdapterOnClickHandler {
        public void onClickTrailer(Trailer trailer);
        public void onClinkStar();
    }

    public DetailAdapter(Movie movie, DetailAdapterOnClickHandler clickHandler) {
        this.movie = movie;
        if (this.movie.isFavorite()){
            this.isFavorite = true;
        }
        this.clickHandler = clickHandler;
        this.trailers = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        int layoutId;

        switch (viewType) {
            case VIEW_TYPE_MOVIE:
                layoutId = R.layout.movie_item;
                break;
            case VIEW_TYPE_TRAILER:
                layoutId = R.layout.trailer_item;
                break;
            case VIEW_TYPE_REVIEW:
                layoutId = R.layout.review_item;
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(layoutId, viewGroup, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder detailViewHolder, int position) {
        if (detailViewHolder.getItemViewType() == VIEW_TYPE_MOVIE) {
            int type = detailViewHolder.getItemViewType();
            detailViewHolder.title.setText(movie.getTitle());
            detailViewHolder.releaseData.setText(movie.getReleaseDateString());
            String userRating = movie.getUserRating() + APPEND_USER_RATING;
            detailViewHolder.userRating.setText(userRating);
            detailViewHolder.plotSynopsis.setText(movie.getPlotSynopsis());
            if ( this.isFavorite ) {
                detailViewHolder.starImage.setImageResource(R.drawable.ic_favorite_on);
            } else {
                detailViewHolder.starImage.setImageResource(R.drawable.ic_favorite_off);
            }

        } else {
            if (detailViewHolder.getItemViewType() == VIEW_TYPE_TRAILER) {
                if (position == 1) {
                    detailViewHolder.divider.setVisibility(View.VISIBLE);
                    detailViewHolder.sectionHeaderLabel.setText(detailViewHolder.sectionHeaderLabel.getContext().getString(R.string.item_header_trailers_label));
                    detailViewHolder.trailerDivider.setVisibility(View.GONE);
                } else {
                    detailViewHolder.divider.setVisibility(View.GONE);
                    detailViewHolder.trailerDivider.setVisibility(View.VISIBLE);
                }
                detailViewHolder.trailerName.setText(trailers.get(position - 1).getName());
            } else {
                if (position == trailers.size() + 1) {
                    detailViewHolder.divider.setVisibility(View.VISIBLE);
                    detailViewHolder.sectionHeaderLabel.setText(detailViewHolder.sectionHeaderLabel.getContext().getString(R.string.item_header_reviews_label));
                    detailViewHolder.reviewDivider.setVisibility(View.GONE);
                } else {
                    detailViewHolder.divider.setVisibility(View.GONE);
                    detailViewHolder.reviewDivider.setVisibility(View.VISIBLE);
                }
                int reviewPosition = position - trailers.size() - 1;
                detailViewHolder.reviewAuthor.setText(reviews.get(reviewPosition).getAuthor());
                detailViewHolder.reviewContent.setText(reviews.get(reviewPosition).getContent());
            }
        }
    }

    @Override
    public int getItemCount() {
        return trailers.size() + reviews.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_MOVIE : position <= trailers.size() ? VIEW_TYPE_TRAILER : VIEW_TYPE_REVIEW;
    }

    public void setDetailLists(List<Trailer> trailers, List<Review> reviews) {
        this.trailers = trailers;
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        if (this.movie.isFavorite()){
            isFavorite = true;
        }
        notifyDataSetChanged();
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder{

        final TextView title;
        final TextView releaseData;
        final TextView userRating;
        final TextView plotSynopsis;
        final ImageView thumbnail;
        final ImageView starImage;

        final View trailerDivider;
        final TextView trailerName;
        final ImageView play;

        final View reviewDivider;
        final TextView reviewAuthor;
        final TextView reviewContent;

        final TextView sectionHeaderLabel;
        final View divider;


        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            // Movie details
            title = itemView.findViewById(R.id.title_tv);
            releaseData = itemView.findViewById(R.id.release_date_tv);
            userRating = itemView.findViewById(R.id.user_rating_tv);
            plotSynopsis = itemView.findViewById(R.id.plot_synopsis_tv);
            thumbnail = itemView.findViewById(R.id.thumbnail_iv);
            starImage = itemView.findViewById(R.id.star_iv);
            if (starImage != null) {
                starImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isFavorite = !isFavorite;
                        clickHandler.onClinkStar();
                        notifyDataSetChanged();
                    }
                });
            }
            if (thumbnail != null) {
                String movieDBImageHost = itemView.getResources().getString(R.string.tmdb_image_base_url);
                String url = movie.getPosterUrl(movieDBImageHost);
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .into(thumbnail);
            }

            // Trailer
            trailerDivider = itemView.findViewById(R.id.trailer_divider);
            trailerName = (TextView) itemView.findViewById(R.id.trailer_name_tv);
            play = (ImageView) itemView.findViewById(R.id.play_iv);
            if (play != null) {
               play.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       int index = getAdapterPosition() - 1;
                       clickHandler.onClickTrailer(trailers.get(index));
                   }
               });
            }

            // Review
            reviewDivider = itemView.findViewById(R.id.review_divider);
            this.reviewAuthor = (TextView) itemView.findViewById(R.id.review_author_tv);
            this.reviewContent = (TextView) itemView.findViewById(R.id.review_content_tv);

            // Section header
            this.sectionHeaderLabel = (TextView) itemView.findViewById(R.id.section_header_label_tv);
            this.divider = (View) itemView.findViewById(R.id.divider_item);

        }

    }
}
