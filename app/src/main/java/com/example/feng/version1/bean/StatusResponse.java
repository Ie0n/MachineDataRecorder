package com.example.feng.version1.bean;

import java.util.List;

public class StatusResponse {

    private DataBean data;
    private int status;
    private StatusInfoBean statusinfo;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public StatusInfoBean getStatusinfo() {
        return statusinfo;
    }

    public void setStatusinfo(StatusInfoBean statusinfo) {
        this.statusinfo = statusinfo;
    }

    public static class DataBean {
        private List<MetersBean> meters;

        public List<MetersBean> getMeters() {
            return meters;
        }

        public void setMeters(List<MetersBean> meters) {
            this.meters = meters;
        }

        public static class MetersBean {

            private int meterId;
            private String meterName;
            private double dataUpper,dataLower;

            public int getMeterId() {
                return meterId;
            }

            public void setMeterId(int meterId) {
                this.meterId = meterId;
            }

            public String getMeterName() {
                return meterName;
            }

            public void setMeterName(String meterName) {
                this.meterName = meterName;
            }

            public double getDataUpper() {
                return dataUpper;
            }

            public void setDataUpper(double dataUpper) {
                this.dataUpper = dataUpper;
            }

            public double getDataLower() {
                return dataLower;
            }

            public void setDataLower(double dataLower) {
                this.dataLower = dataLower;
            }
        }
    }

    public static class StatusInfoBean {

        private String message;
        private String detail;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }
}
