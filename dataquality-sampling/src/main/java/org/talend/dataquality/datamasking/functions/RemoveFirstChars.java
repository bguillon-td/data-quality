// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.functions;

/**
 * created by jgonzalez on 22 juin 2015. This function will remove the n first chars of the input.
 *
 */
public abstract class RemoveFirstChars<T> extends CharactersOperation<T> {

    private static final long serialVersionUID = 767660772520822572L;

    @Override
    protected void initAttributes() {
        endIndex = Integer.parseInt(parameters[0]);
        toRemove = true;
    }

    @Override
    protected boolean validParameters() {
        return CharactersOperationUtils.validParameters1Number(parameters);
    }
}
