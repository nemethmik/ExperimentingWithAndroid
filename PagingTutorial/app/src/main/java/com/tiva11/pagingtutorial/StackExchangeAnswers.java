package com.tiva11.pagingtutorial;

import java.util.Date;
import java.util.List;

public class StackExchangeAnswers {
    class Owner {
        public int reputation;
        public long user_id;
        public String user_type;
        public String profile_image;
        public String display_name;
        public String link;
    }
    class Item {
        public Owner owner;
        public boolean is_accepted;
        public int score;
        public long last_activity_date; //Date type doesn't work unfortunately
        public long last_edit_date;
        public long creation_date;
        public long answer_id;
        public long question_id;
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Item) return answer_id == ((Item)obj).answer_id;
            else return super.equals(obj);
        }
    }
    public List<Item> items;
    public boolean has_more;
    public int quota_max;
    public int quota_remaining;
}
