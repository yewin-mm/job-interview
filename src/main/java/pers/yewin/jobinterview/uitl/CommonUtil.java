package pers.yewin.jobinterview.uitl;

import helper.ResponseUtil;
import helper.ValidationUtil;
import org.springframework.data.domain.Sort;
import pers.yewin.jobinterview.model.pojo.FilterTypeCount;
import pers.yewin.jobinterview.model.pojo.SortBy;
import pers.yewin.jobinterview.model.request.JobDataRequest;
import pojo.ServiceResponse;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static helper.ConstantUtil.FAIL_MESSAGE;
import static helper.ConstantUtil.SUCCESS_MESSAGE;
import static helper.ValidationUtil.isEmptyCollection;
import static helper.ValidationUtil.isEmptyString;
import static pers.yewin.jobinterview.uitl.ConstantUtil.*;
import static pers.yewin.jobinterview.uitl.ConstantUtil.COMMA_REGEX;
import static pers.yewin.jobinterview.uitl.ValidationUtil.isValidOrder;
import static pers.yewin.jobinterview.uitl.ValidationUtil.isValidSortKeyValue;

/**
 * author: Ye Win,
 * project: job-interview,
 * package: pers.yewin.jobinterview.uitl
 */

public class CommonUtil {
    CommonUtil(){}
    public static boolean isValidNumber(String value){
        try{
            Integer.parseInt(value);
            return true;
        }catch (NumberFormatException ne){
            return false;
        }
    }


    // this method is to get sort by
    public static ServiceResponse getSortBy(List<String> sortFields){

        if(isEmptyCollection(sortFields))
            sortFields.add("jobTitle,desc"); // default order

        if(sortFields.size()>3){
            return ResponseUtil.getResponseObj(FAIL_MESSAGE, MAXIMUM_COUNT, null, THAI_ZONE_ID);
        }

        if(sortFields.size()==2){ // to encounter if only one sort type was come, for that input list size will be 2, and we need to combine as comma
            String order = sortFields.get(1);
            String field = sortFields.get(0);

            if (isEmptyString(field) && isValidOrder(order)) {
                return ResponseUtil.getResponseObj(FAIL_MESSAGE, SORT_FIELD_INVALID, null, THAI_ZONE_ID);
            }

            if (isValidOrder(order)) {
                sortFields.set(0, field + "," + order);
                sortFields.remove(1);
            }
        }

        return getCompleteOrder(sortFields);
    }

    private static ServiceResponse getCompleteOrder(List<String> sortFields){
        int[] count = { 0 };
        List<Sort.Order> orders = sortFields.stream()
                .map(sortField -> {
                    String[] split = sortField.split(COMMA_REGEX);
                    if(!isValidSortKeyValue(split)) {
                        return null;
                    } else {
                        String field = split[0];
                        field = fixJobTitle(field);
                        if("asc".equalsIgnoreCase(split[1])) {
                            return Sort.Order.asc(field);
                        } else {
                            return Sort.Order.desc(field);
                        }
                    }
                })
                .peek(order -> {
                    if (order != null) {
                        count[0]++; // increment the count for each non-null order
                    }
                })
                .filter(Objects::nonNull) // remove any null orders from the stream
                .collect(Collectors.toList()); // collect the remaining orders into a list

        if(count[0]==0){
            return ResponseUtil.getResponseObj(FAIL_MESSAGE, SORT_ORDER_INVALID, null, THAI_ZONE_ID);
        }

        return ResponseUtil.getResponseObj(SUCCESS_MESSAGE, "", orders, THAI_ZONE_ID);
    }

    private static String fixJobTitle(String field){
        if(field.equalsIgnoreCase(SortBy.JOB_TITLE.getValue()) || field.equalsIgnoreCase(SortBy.JOB_TITLE_FIELD.getValue())){
            return SortBy.JOB_TITLE_FIELD.getValue();
        }
        return field;
    }

    // this method is to get filter list
    public static String getFilter(JobDataRequest request){
        if(ValidationUtil.isEmptyString(request.getFilterFields())){
            return ALL;
        }
        String [] filterList = request.getFilterFields().split(COMMA_REGEX);
        if(filterList.length==1) {
            FilterTypeCount typeCount = new FilterTypeCount(0,0,0,0);
            getCount(request.getFilterFields(), typeCount);
            return getSingleFilter(typeCount);

        }else if(filterList.length==2){
            FilterTypeCount typeCount = new FilterTypeCount(0,0,0,0);
            for(String filter: filterList){
                getCount(filter, typeCount);
            }
            return get2FilterType(typeCount);

        }else if(filterList.length==3){
            FilterTypeCount typeCount = new FilterTypeCount(0,0,0,0);
            for(String filter: filterList){
                getCount(filter, typeCount);
            }
            return get3FilterType(typeCount);

        }else {
            FilterTypeCount typeCount = new FilterTypeCount(0,0,0,0);
            for(String filter: filterList){
                getCount(filter, typeCount);
            }
            return getManyFilterType(typeCount);
        }

    }


    private static String getSingleFilter(FilterTypeCount typeCount) {
        if(typeCount.getJob() > 0)
            return JOB;
        if(typeCount.getSalary() > 0 )
            return SALARY;
        if(typeCount.getGender() > 0)
            return GENDER;

        return UNKNOWN; // if we can't recognize the filter, we will go with all fields display
    }

    private static String get2FilterType(FilterTypeCount typeCount) {
        if(typeCount.getJob() == 1 && typeCount.getSalary() == 1)
            return JOB_AND_SALARY;
        if(typeCount.getJob() == 1 && typeCount.getGender() == 1)
            return JOB_AND_GENDER;
        if(typeCount.getSalary() == 1 && typeCount.getGender() == 1)
            return SALARY_AND_GENDER;

        return getSingleFilter(typeCount);
    }

    private static String get3FilterType(FilterTypeCount typeCount) {
        if(typeCount.getJob() == 1 && typeCount.getSalary() == 1 && typeCount.getGender() == 1)
            return ALL;

        if(typeCount.getJob() == 1 && typeCount.getSalary() == 1 && typeCount.getGender() == 0)
            return JOB_AND_SALARY;

        if(typeCount.getJob() == 1 && typeCount.getSalary() == 0 && typeCount.getGender() == 1)
            return JOB_AND_GENDER;

        if(typeCount.getJob() == 0 && typeCount.getSalary() == 1 && typeCount.getGender() == 1)
            return SALARY_AND_GENDER;

        return getSingleFilter(typeCount);
    }

    private static String getManyFilterType(FilterTypeCount typeCount) {
        if(typeCount.getJob() > 0 && typeCount.getSalary() > 0 && typeCount.getGender() > 0)
            return ALL;

        if(typeCount.getJob() > 0 && typeCount.getSalary() > 0 && typeCount.getGender() == 0)
            return JOB_AND_SALARY;

        if(typeCount.getJob() > 0 && typeCount.getSalary() == 0 && typeCount.getGender() > 0)
            return JOB_AND_GENDER;

        if(typeCount.getJob() == 0 && typeCount.getSalary() > 1 && typeCount.getGender() > 1)
            return SALARY_AND_GENDER;

        return getSingleFilter(typeCount);
    }


    // this method is to calculate how many text are repeated
    private static void getCount(String filter, FilterTypeCount typeCount){

        if (filter.equalsIgnoreCase(SortBy.JOB_TITLE.getValue()) || filter.equalsIgnoreCase(SortBy.JOB_TITLE_FIELD.getValue())) {
            typeCount.setJob(typeCount.getJob() + 1);
        } else if (filter.equalsIgnoreCase(SortBy.SALARY.getValue())) {
            typeCount.setSalary(typeCount.getSalary() + 1);
        } else if (filter.equalsIgnoreCase(SortBy.GENDER.getValue())) {
            typeCount.setGender(typeCount.getGender() + 1);
        }else
            typeCount.setUnknown(typeCount.getUnknown() + 1);

    }
}
