package com.tiva11.food2fork;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchRecipesResponse implements Parcelable
{
    public String error = null; //Added for easy error handling in API calls
    public static SearchRecipesResponse createError(String error) {
        SearchRecipesResponse r = new SearchRecipesResponse();
        r.error = error;
        return r;
    }
    @SerializedName("count")
    @Expose
    public long count;
    @SerializedName("recipes")
    @Expose
    public List<Recipe> recipes = null;
    public final static Parcelable.Creator<SearchRecipesResponse> CREATOR = new Creator<SearchRecipesResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public SearchRecipesResponse createFromParcel(Parcel in) {
            return new SearchRecipesResponse(in);
        }

        public SearchRecipesResponse[] newArray(int size) {
            return (new SearchRecipesResponse[size]);
        }

    }
            ;

    protected SearchRecipesResponse(Parcel in) {
        this.count = ((long) in.readValue((long.class.getClassLoader())));
        in.readList(this.recipes, (com.tiva11.food2fork.Recipe.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public SearchRecipesResponse() {
    }

    /**
     *
     * @param count
     * @param recipes
     */
    public SearchRecipesResponse(long count, List<Recipe> recipes) {
        super();
        this.count = count;
        this.recipes = recipes;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(count);
        dest.writeList(recipes);
    }

    public int describeContents() {
        return 0;
    }

}