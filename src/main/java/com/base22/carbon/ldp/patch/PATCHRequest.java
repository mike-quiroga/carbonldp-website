package com.base22.carbon.ldp.patch;

import com.base22.carbon.ldp.models.RDFResource;

public interface PATCHRequest extends RDFResource {
	public AddAction[] getAddActions();

	public SetAction[] getSetActions();

	public DeleteAction[] getDeleteActions();
}
