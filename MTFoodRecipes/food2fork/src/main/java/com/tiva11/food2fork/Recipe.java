 package com.tiva11.food2fork;

        import java.util.Date;
        import java.util.List;
        import android.os.Parcel;
        import android.os.Parcelable;
        import android.os.Parcelable.Creator;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class Recipe implements Parcelable {
    @SerializedName("publisher")
    @Expose
    public String publisher;
    @SerializedName("f2f_url")
    @Expose
    public String f2fUrl;
    @SerializedName("ingredients")
    @Expose
    public List<String> ingredients = null;
    @SerializedName("source_url")
    @Expose
    public String sourceUrl;
    @SerializedName("recipe_id")
    @Expose
    public String recipeId;
    @SerializedName("image_url")
    @Expose
    public String imageUrl;
    @SerializedName("social_rank")
    @Expose
    public double socialRank;
    @SerializedName("publisher_url")
    @Expose
    public String publisherUrl;
    @SerializedName("title")
    @Expose
    public String title;
    public final static Parcelable.Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }
        public Recipe[] newArray(int size) {
            return (new Recipe[size]);
        }
    };
    protected Recipe(Parcel in) {
        this.publisher = ((String) in.readValue((String.class.getClassLoader())));
        this.f2fUrl = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.ingredients, (java.lang.String.class.getClassLoader()));
        this.sourceUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.recipeId = ((String) in.readValue((String.class.getClassLoader())));
        this.imageUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.socialRank = ((double) in.readValue((double.class.getClassLoader())));
        this.publisherUrl = ((String) in.readValue((String.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
    }
    public Recipe() { }
//Application specific members, not from Food2Fork API
    public boolean isFavorite;
    public Date favoriteTime;

    /**
     *
     * @param ingredients
     * @param socialRank
     * @param title
     * @param imageUrl
     * @param recipeId
     * @param sourceUrl
     * @param f2fUrl
     * @param publisherUrl
     * @param publisher
     */
    public Recipe(String publisher, String f2fUrl, List<String> ingredients, String sourceUrl, String recipeId, String imageUrl, double socialRank, String publisherUrl, String title) {
        super();
        this.publisher = publisher;
        this.f2fUrl = f2fUrl;
        this.ingredients = ingredients;
        this.sourceUrl = sourceUrl;
        this.recipeId = recipeId;
        this.imageUrl = imageUrl;
        this.socialRank = socialRank;
        this.publisherUrl = publisherUrl;
        this.title = title;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(publisher);
        dest.writeValue(f2fUrl);
        dest.writeList(ingredients);
        dest.writeValue(sourceUrl);
        dest.writeValue(recipeId);
        dest.writeValue(imageUrl);
        dest.writeValue(socialRank);
        dest.writeValue(publisherUrl);
        dest.writeValue(title);
    }

    public int describeContents() {
        return 0;
    }

}