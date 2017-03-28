/*
 * #%L
 * carewebframework
 * %%
 * Copyright (C) 2008 - 2017 Regenstrief Institute, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related
 * Additional Disclaimer of Warranty and Limitation of Liability available at
 *
 *      http://www.carewebframework.org/licensing/disclaimer.
 *
 * #L%
 */
package org.carewebframework.vista.mbroker;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.carewebframework.vista.mbroker.FMDate;

import org.junit.Test;

public class DateTest {
    
    @Test
    public void testDateConversion() throws ParseException {
        testDates("2890525.124506", "05/25/1989 12:45:06", "MM/dd/yyyy HH:mm:ss");
        testDates("3090228.02030099", "02/28/2009 02:03:00:99", "MM/dd/yyyy HH:mm:ss:SS");
        testDates("2290920", "09/20/1929", "MM/dd/yyyy");
        testDates("2890525.2403", "05/26/1989 00:03", "MM/dd/yyyy HH:mm");
    }
    
    private void testDates(String FMDateStr, String refDate, String format) throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        FMDate date = new FMDate(FMDateStr);
        Date date2 = fmt.parse(refDate);
        assertEquals(date, date2);
        String FMDateStr2 = date.getFMDate();
        assertEquals(FMDateStr, FMDateStr2);
        print(date);
        print(date.toStringDateOnly());
        print(date.toStringFull());
        print("---------------");
    }
    
    private void print(Object object) {
        System.out.println(object);
    }
    
}
