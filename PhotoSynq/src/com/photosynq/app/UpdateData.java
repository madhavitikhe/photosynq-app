package com.photosynq.app;

import com.photosynq.app.HTTP.PhotosynqResponse;

public class UpdateData implements PhotosynqResponse{

	@Override
	public void onResponseReceived(String result) {
		System.out.println("data update result :"+result);
		
	}

}
