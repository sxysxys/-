package com.shen.shengeunion.model;

import com.shen.shengeunion.model.domain.Categories;
import com.shen.shengeunion.model.domain.HomePagerContent;
import com.shen.shengeunion.model.domain.OnSellContent;
import com.shen.shengeunion.model.domain.RecommendResult;
import com.shen.shengeunion.model.domain.SearchResult;
import com.shen.shengeunion.model.domain.SelectedContent;
import com.shen.shengeunion.model.domain.SelectedPageCategories;
import com.shen.shengeunion.model.domain.TicketResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 使用retrofit框架拿数据
 */
public interface Api {

    @GET("discovery/categories")
    Call<Categories> getCategories();

    @GET("recommend/categories")
    Call<SelectedPageCategories> getSelectedPageCategories();

    /**
     * 获取推荐
     * @return
     */
    @GET("search/recommend")
    Call<RecommendResult> getRecommendResult();

    @GET
    Call<SelectedContent> getContent(@Url String url);

    @GET
    Call<HomePagerContent> getHomePagerContent(@Url String url);

    @GET("search")
    Call<SearchResult> getSearchResult(@Query("page") int page, @Query("keyword") String keyWord);

    @GET
    Call<OnSellContent> getOnSellContent(@Url String url);

    @POST("tpwd")
    Call<TicketResult> getTicket(@Body TicketParams ticketParams);


}
