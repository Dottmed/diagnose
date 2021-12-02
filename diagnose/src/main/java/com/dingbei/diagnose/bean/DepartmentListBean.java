package com.dingbei.diagnose.bean;

import java.util.List;

/**
 * @author Dayo
 * @desc 全科医生列表bean
 */

public class DepartmentListBean {

    private List<ResultsBean> results;

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public static class ResultsBean {
        private String name;
        private String id;

        public void setName(String name) {
            this.name = name;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

    }
}
