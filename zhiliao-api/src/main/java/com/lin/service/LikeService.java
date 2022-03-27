package com.lin.service;


import com.lin.vo.params.LikeParam;

public interface LikeService {


    Long findLikeNum(LikeParam likeParam) ;

    boolean findLike(LikeParam likeParam) ;

    boolean selectLike(LikeParam likeParam) ;
}
