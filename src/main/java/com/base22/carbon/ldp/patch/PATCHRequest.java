package com.base22.carbon.ldp.patch;

import com.base22.carbon.ldp.models.LDPResource;

public interface PATCHRequest extends LDPResource {
	public AddAction[] getAddActions();

	public SetAction[] getSetActions();

	public DeleteAction[] getDeleteActions();
}
