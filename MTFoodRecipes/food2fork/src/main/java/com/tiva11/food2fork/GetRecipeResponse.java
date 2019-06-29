package com.tiva11.food2fork;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetRecipeResponse implements Parcelable
{

    @SerializedName("recipe")
    @Expose
    public Recipe recipe;
    public final static Parcelable.Creator<GetRecipeResponse> CREATOR = new Creator<GetRecipeResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GetRecipeResponse createFromParcel(Parcel in) {
            return new GetRecipeResponse(in);
        }

        public GetRecipeResponse[] newArray(int size) {
            return (new GetRecipeResponse[size]);
        }

    }
            ;

    protected GetRecipeResponse(Parcel in) {
        this.recipe = ((Recipe) in.readValue((Recipe.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public GetRecipeResponse() {
    }

    /**
     *
     * @param recipe
     */
    public GetRecipeResponse(Recipe recipe) {
        super();
        this.recipe = recipe;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(recipe);
    }

    public int describeContents() {
        return 0;
    }

}
