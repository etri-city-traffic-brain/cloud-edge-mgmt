package com.innogrid.uniq.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class Pagination {

    private static final Logger logger = LoggerFactory.getLogger(Pagination.class);

    public static final int PAGE_GROUP = 10;

    public static final int ROW_PER_PAGE = 10;

    /**
     *
     * @param page 현재페이지
     * @param total 총 레코드 수
     * @param rowPerPage 페이지당 보여줄 레코드 수
     */
    public static Map<String, Object> getPagination(Integer page, Integer total, Integer rowPerPage, Integer pageGroup, List data) {

        if(page == null)
            page = 1;

        if(total == null)
            total = 1;

        if(rowPerPage == null)
            rowPerPage = ROW_PER_PAGE;

        if(pageGroup == null)
            pageGroup = PAGE_GROUP;


        int totalPage = (int) Math.ceil( total / (double) rowPerPage);

        if(totalPage <= 0)
            totalPage = 1;

        int groupNo = page/pageGroup + ( page  % pageGroup >0 ? 1:0);

        int endPage = groupNo * pageGroup;

        int startPage = endPage - (pageGroup - 1);

        if(endPage > totalPage){
            endPage = totalPage;
        }
/*
        int beforePage = pageStart - pageGroup;
        int nextPage = pageStart + pageGroup;

        if(beforePage < 1){
            beforePage =1;
        }

        if(nextPage >totalPage) {
            nextPage = totalPage / pageGroup * pageGroup + 1;
        }
        */


//        if(logger.isDebugEnabled()) {
//            logger.debug("ROW_PER_PAGE:{}, PAGE_GROUP:{}, currentPage:{}, total:{}, totalPage:{}, startPage:{}, endPage: {}", rowPerPage, pageGroup, page, total, totalPage, startPage, endPage);
//        }


        Map<String, Object> pageInfo = new HashMap();
        pageInfo.put("records", total);
        pageInfo.put("page", page);
        pageInfo.put("startPage", startPage);
        pageInfo.put("endPage", endPage);
        pageInfo.put("total", totalPage);

        if(data != null) {
            pageInfo.put("rows", data);
        }

        return pageInfo;

    }

    /**
     *
     * @param page 현재페이지
     * @param total 총 레코드 수
     * @param rowPerPage 페이지당 보여줄 레코드 수
     * @param data 데이터
     */
    public static Map<String, Object> getPagination(Integer page, Integer total, Integer rowPerPage, List data) {
        return getPagination(page, total, rowPerPage, PAGE_GROUP, data);
    }

    /**
     *
     * @param page 현재페이지
     * @param total 총 레코드 수
     * @param rowPerPage 페이지당 보여줄 레코드 수
     */
    public static Map<String, Object> getPagination(Integer page, Integer total, Integer rowPerPage) {
        return getPagination(page, total, rowPerPage, PAGE_GROUP, null);
    }

    /**
     *
     * @param page 현재페이지
     * @param total 총 레코드
     * @param data 데이터
     */
    public static Map<String, Object> getPagination(Integer page, Integer total, List data) {
        return getPagination(page, total, ROW_PER_PAGE, PAGE_GROUP, data);
    }

    /**
     *
     * @param page 현재페이지
     * @param total 총 레코드
     */
    public static Map<String, Object> getPagination(Integer page, Integer total) {
        return getPagination(page, total, ROW_PER_PAGE, PAGE_GROUP, null);
    }


//    public static void main(String[] args) {

//        Pagination.getPagination(1, 1, 10, 5, null);
//        Pagination.getPagination(1, 54, 10, 5, null);
//        Pagination.getPagination(2, 54, 10, 5, null);
//        Pagination.getPagination(3, 54, 10, 5, null);
//        Pagination.getPagination(4, 54, 10, 5, null);
//        Pagination.getPagination(5, 54, 10, 5, null);
//        Pagination.getPagination(6, 54, 10, 5, null);
//    }


    public static void sort(List<?> list, Function function, String sord) {
        if(sord.equals("asc")) {
            list.sort(Comparator.comparing(function));
        } else {
            list.sort(Comparator.comparing(function).reversed());
        }
    }
}
