/*
 *
 *
 *  Copyright (C) 2018 IHS Markit.
 *  All Rights Reserved
 *
 *
 *  NOTICE:  All information contained herein is, and remains
 *  the property of IHS Markit and its suppliers,
 *  if any.  The intellectual and technical concepts contained
 *  herein are proprietary to IHS Markit and its suppliers
 *  and may be covered by U.S. and Foreign Patents, patents in
 *  process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from IHS Markit.
 */

package org.fdc3.appd.poc.exceptions;

/**
 * A serious fault which renders the API unusable.
 * <p>
 * A reasonable response to such a fault is to terminate the program.
 *
 * @author bruce.skingle
 */
public class ProgramFault extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProgramFault() {
    }

    public ProgramFault(String message) {
        super(message);
    }

    public ProgramFault(Throwable cause) {
        super(cause);
    }

    public ProgramFault(String message, Throwable cause) {
        super(message, cause);
    }

    public ProgramFault(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
