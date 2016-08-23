package com.parker.adsdk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by parker on 2016/8/23.completed
 */
public class IOUtil {
    public static String cat(InputStream in, Map map){
        boolean contain = false;
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        if (map == null || map.size() < 1)
        {
            contain = false;
        }
        while (true)
        {
            try {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                if (map != null) {
                    if (line.contains(":")) {
                        String[] split = line.split(":");
                        if (contain) {
                            updateMap(map, split[0], split[1]);
                        } else if (split[0].trim().equals("processor")) {
                            map.put("processorcnt", split[1].trim());
                        } else {
                            map.put(split[0].trim(), split[1].trim());
                        }
                    }
                }
                sb.append(line);
                sb.append("\n");
            } catch (IOException e) {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (Throwable tr) {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e3) {
                    }
                }
            }



        }
        if (br != null) {
            try {
                br.close();
            } catch (IOException e4) {
            }
        }
        return sb.toString();
    }


    private static void updateMap(Map map, String key, String value) {
        Object next;
        String keyt = key.replace("[", "").replace("]", "").trim();
        String valuet = value.replace("[", "").replace("]", "").trim();
        Iterator it = map.keySet().iterator();
        if(it != null && (it.hasNext())) {
            do {
                if(!it.hasNext()) {
                    return;
                }

                next = it.next();
            }
            while(!((String)next).equals(keyt));

            map.put(next, valuet);
        }
    }

}
