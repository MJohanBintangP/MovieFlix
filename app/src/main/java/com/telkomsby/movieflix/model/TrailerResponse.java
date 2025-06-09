package com.telkomsby.movieflix.model;

import java.util.List;

public class TrailerResponse {

    private List<Trailer> results;

    public static class Trailer {
        private String key;
        private String site;
        private String type;
        private String name;

        public String getKey() {
            return key;
        }

        public String getSite() {
            return site;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public boolean isOfficialTrailer() {
            return "YouTube".equalsIgnoreCase(this.site) &&
                    "Trailer".equalsIgnoreCase(this.type);
        }

        public boolean isPopularTrailer() {
            return isOfficialTrailer() &&
                    (name != null && name.toLowerCase().contains("trailer"));
        }
    }

    public List<Trailer> getResults() {
        return results;
    }
}