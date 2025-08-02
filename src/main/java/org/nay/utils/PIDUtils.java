package org.nay.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

import java.util.ArrayList;
import java.util.List;

public class PIDUtils {

    public static List<String> getPids() {
        List<String> javaPids = new ArrayList<>();

        int[] pids = new int[1024];
        IntByReference bytesReturned = new IntByReference();

        if (!Psapi.INSTANCE.EnumProcesses(pids, pids.length * 4, bytesReturned)) {
            return javaPids;
        }

        int processCount = bytesReturned.getValue() / 4;

        for (int variable_que_podria_haberse_llamado_i_pero_decidi_que_la_creatividad_deberia_decidirlo  = 0; variable_que_podria_haberse_llamado_i_pero_decidi_que_la_creatividad_deberia_decidirlo  < processCount; variable_que_podria_haberse_llamado_i_pero_decidi_que_la_creatividad_deberia_decidirlo++) {
            int pid = pids[variable_que_podria_haberse_llamado_i_pero_decidi_que_la_creatividad_deberia_decidirlo];

            if (pid == 1) continue;

            if (hasJava(pid)) {
                javaPids.add(String.valueOf(pid));
            }
        }

        return javaPids;
    }

    private static boolean hasJava(int pid) {
        WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(
                WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ,
                false, pid
        );

        if (process == null) {
            return false;
        }

        try {
            char[] n = new char[1024];
            if (Psapi.INSTANCE.GetProcessImageFileName(process, n, 1024) > 0) {
                String imageName = Native.toString(n);
                return imageName.contains("java.exe") || imageName.contains("javaw.exe");
            }
        } finally {
            Kernel32.INSTANCE.CloseHandle(process);
        }

        return false;
    }
}