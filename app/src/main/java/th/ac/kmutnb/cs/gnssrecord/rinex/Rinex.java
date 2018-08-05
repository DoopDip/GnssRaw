package th.ac.kmutnb.cs.gnssrecord.rinex;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import th.ac.kmutnb.cs.gnssrecord.R;
import th.ac.kmutnb.cs.gnssrecord.config.Constants;
import th.ac.kmutnb.cs.gnssrecord.model.RinexData;
import th.ac.kmutnb.cs.gnssrecord.model.RinexHeader;

public class Rinex {

    private static final String TAG = Rinex.class.getSimpleName();

    private FileWriter out = null;
    private char line[] = new char[81];

    private Context context;
    private int ver;

    public Rinex(Context context, int ver) {
        this.context = context;
        this.ver = ver;
        createFile();
    }

    public void closeFile() {
        Log.i(TAG, "CloseFile");
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetLine() {
        for (int i = 0; i < 80; i++)
            line[i] = ' ';
    }

    private void writeLine(String label) {
        for (int i = 0; i < label.length(); i++)
            line[i + 60] = label.charAt(i);
        try {
            line[80] = '\n';
            out.write(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile() {
        Date date = new Date();

        String dateString = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(date);
        String type = "o"; //Observable file
        int year = Integer.parseInt(new SimpleDateFormat("yy", Locale.US).format(date));
        String yearString;
        if (year - 10 < 0)
            yearString = "0" + year;
        else
            yearString = "" + year;
        String fileName = "GN" + dateString + ver + "." + yearString + type;

        try {
            File rootFile = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name) + "_Rinex");
            if (!rootFile.exists()) rootFile.mkdirs();

            File file = new File(rootFile, fileName);
            out = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "CreateFile, File name = " + fileName);
    }

    public void writeHeader(RinexHeader rinexHeader) { //header labels in columns 61-80
        Log.i(TAG, "WriteHeader");
        Date date = new Date(rinexHeader.getGpsTime() + 1000);

        //RINEX VERSION / TYPE
        resetLine();
        String version = "2.11";
        if (ver == Constants.VER_3_03) version = "3.03";
        String type = "OBSERVATION DATA";
        String source = "M: Mixed";
        for (int i = 0; i < version.length(); i++)
            line[i + 5] = version.charAt(i);
        for (int i = 0; i < type.length(); i++)
            line[i + 20] = type.charAt(i);
        for (int i = 0; i < source.length(); i++)
            line[i + 40] = source.charAt(i);
        writeLine("RINEX VERSION / TYPE");

        //PGM / RUN BY / DATE
        resetLine();
        String program = "GnssRecord";
        String agency = "KMUTNB";
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd hhmmss", Locale.US);
        formatDate.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateCreation = formatDate.format(new Date()) + " UTC";
        for (int i = 0; i < program.length(); i++)
            line[i] = program.charAt(i);
        for (int i = 0; i < agency.length(); i++)
            line[i + 20] = agency.charAt(i);
        for (int i = 0; i < dateCreation.length(); i++)
            line[i + 40] = dateCreation.charAt(i);
        writeLine("PGM / RUN BY / DATE");

        //COMMENT
        resetLine();
        for (int i = 2; i < 58; i++)
            line[i] = '-';
        writeLine("COMMENT");
        resetLine();
        String commentLineOne = "Generated by GnssRecord application.";
        for (int i = 0; i < commentLineOne.length(); i++)
            line[i + 9] = commentLineOne.charAt(i);
        writeLine("COMMENT");
        resetLine();
        String commentLineTwo = "This application belong to CS#28 at KMUTNB.";
        for (int i = 0; i < commentLineTwo.length(); i++)
            line[i + 9] = commentLineTwo.charAt(i);
        writeLine("COMMENT");
        resetLine();
        for (int i = 2; i < 58; i++)
            line[i] = '-';
        writeLine("COMMENT");


        //MARKER NAME
        resetLine();
        for (int i = 0; i < rinexHeader.getMarkName().length(); i++)
            line[i] = rinexHeader.getMarkName().charAt(i);
        writeLine("MARKER NAME");

        //MARKER TYPE
        if (ver == Constants.VER_3_03) {
            resetLine();
            for (int i = 0; i < rinexHeader.getMarkType().length(); i++)
                line[i] = rinexHeader.getMarkType().toUpperCase().charAt(i);
            writeLine("MARKER TYPE");
        }

        //OBSERVER / AGENCY
        resetLine();
        for (int i = 0; i < rinexHeader.getObserverName().length(); i++)
            line[i] = rinexHeader.getObserverName().charAt(i);
        for (int i = 0; i < rinexHeader.getObserverAgencyName().length(); i++)
            line[i + 20] = rinexHeader.getObserverAgencyName().charAt(i);
        writeLine("OBSERVER / AGENCY");


        //REC # / TYPE / VERS
        resetLine();
        for (int i = 0; i < rinexHeader.getReceiverNumber().length(); i++)
            line[i] = rinexHeader.getReceiverNumber().charAt(i);
        for (int i = 0; i < rinexHeader.getReceiverType().length(); i++)
            line[i + 20] = rinexHeader.getReceiverType().charAt(i);
        for (int i = 0; i < rinexHeader.getReceiverVersion().length(); i++)
            line[i + 40] = rinexHeader.getReceiverVersion().charAt(i);
        writeLine("REC # / TYPE / VERS");


        //ANT # / TYPE
        resetLine();
        for (int i = 0; i < rinexHeader.getAntennaNumber().length(); i++)
            line[i] = rinexHeader.getAntennaNumber().charAt(i);
        for (int i = 0; i < rinexHeader.getAntennaType().length(); i++)
            line[i + 20] = rinexHeader.getAntennaType().charAt(i);
        writeLine("ANT # / TYPE");

        //APPROX POSITION XYZ
        resetLine();
        String xPosition = rinexHeader.getCartesianX();
        String yPosition = rinexHeader.getCartesianY();
        String zPosition = rinexHeader.getCartesianZ();
        for (int i = 0; i < xPosition.length(); i++)
            line[13 - i] = xPosition.charAt(xPosition.length() - 1 - i);
        for (int i = 0; i < yPosition.length(); i++)
            line[27 - i] = yPosition.charAt(yPosition.length() - 1 - i);
        for (int i = 0; i < zPosition.length(); i++)
            line[41 - i] = zPosition.charAt(zPosition.length() - 1 - i);
        writeLine("APPROX POSITION XYZ");

        //ANTENNA: DELTA H/E/N
        resetLine();
        String hDelta = String.format(Locale.US, "%.4f", rinexHeader.getAntennaHeight());
        String eDelta = String.format(Locale.US, "%.4f", rinexHeader.getAntennaEccentricityEast());
        String nDelta = String.format(Locale.US, "%.4f", rinexHeader.getAntennaEccentricityNorth());
        for (int i = 0; i < hDelta.length(); i++)
            line[13 - i] = hDelta.charAt(hDelta.length() - 1 - i);
        for (int i = 0; i < eDelta.length(); i++)
            line[27 - i] = eDelta.charAt(eDelta.length() - 1 - i);
        for (int i = 0; i < nDelta.length(); i++)
            line[41 - i] = nDelta.charAt(nDelta.length() - 1 - i);
        writeLine("ANTENNA: DELTA H/E/N");

        if (ver == Constants.VER_2_11) {
            //# / TYPES OF OBSERV
            String typeOfObserv = "4    C1    L1    S1    D1";
            resetLine();
            for (int i = 0; i < typeOfObserv.length(); i++)
                line[i + 5] = typeOfObserv.charAt(i);
            writeLine("# / TYPES OF OBSERV");
        } else if (ver == Constants.VER_3_03) {
            //SYS / # / OBS TYPES
            String gSys = "G    4 C1C L1C D1C S1C";
            String rSys = "R    4 C1C L1C D1C S1C";
            String eSys = "E    4 C1B L1B D1B S1B";
            String cSys = "C    4 C2I L2I D2I S2I";
            String jSys = "J    4 C1C L1C D1C S1C";
            resetLine();
            for (int i = 0; i < gSys.length(); i++)
                line[i] = gSys.charAt(i);
            writeLine("SYS / # / OBS TYPES");
            resetLine();
            for (int i = 0; i < rSys.length(); i++)
                line[i] = rSys.charAt(i);
            writeLine("SYS / # / OBS TYPES");
            resetLine();
            for (int i = 0; i < eSys.length(); i++)
                line[i] = eSys.charAt(i);
            writeLine("SYS / # / OBS TYPES");
            resetLine();
            for (int i = 0; i < cSys.length(); i++)
                line[i] = cSys.charAt(i);
            writeLine("SYS / # / OBS TYPES");
            resetLine();
            for (int i = 0; i < jSys.length(); i++)
                line[i] = jSys.charAt(i);
            writeLine("SYS / # / OBS TYPES");
        }

        //TIME OF FIRST OBS
        resetLine();
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy", Locale.US);
        SimpleDateFormat formatMonth = new SimpleDateFormat("M", Locale.US);
        SimpleDateFormat formatDay = new SimpleDateFormat("d", Locale.US);
        SimpleDateFormat formatHour = new SimpleDateFormat("H", Locale.US);
        SimpleDateFormat formatMin = new SimpleDateFormat("m", Locale.US);
        SimpleDateFormat formatSec = new SimpleDateFormat("ss.SSSSSSS", Locale.US);

        String year = formatYear.format(date);
        String month = formatMonth.format(date);
        String day = formatDay.format(date);
        String hour = formatHour.format(date);
        String min = formatMin.format(date);
        String sec = formatSec.format(date);
        String system = "GPS";
        for (int i = 0; i < year.length(); i++)
            line[5 - i] = year.charAt(year.length() - 1 - i);
        for (int i = 0; i < month.length(); i++)
            line[11 - i] = month.charAt(month.length() - 1 - i);
        for (int i = 0; i < day.length(); i++)
            line[17 - i] = day.charAt(day.length() - 1 - i);
        for (int i = 0; i < hour.length(); i++)
            line[23 - i] = hour.charAt(hour.length() - 1 - i);
        for (int i = 0; i < min.length(); i++)
            line[29 - i] = min.charAt(min.length() - 1 - i);
        for (int i = 0; i < sec.length(); i++)
            line[42 - i] = sec.charAt(sec.length() - 1 - i);
        for (int i = 0; i < system.length(); i++)
            line[50 - i] = system.charAt(system.length() - 1 - i);
        writeLine("TIME OF FIRST OBS");

        if (ver == Constants.VER_3_03) {
            //SYS / PHASE SHIFTS
            char satelliteIdentifier[] = {'G', 'R', 'E', 'C', 'J'};
            resetLine();
            for (char aSatelliteIdentifier : satelliteIdentifier) {
                line[0] = aSatelliteIdentifier;
                writeLine("SYS / PHASE SHIFTS");
            }
        }

        //END OF HEADER
        resetLine();
        writeLine("END OF HEADER");
    }

    public void writeData(List<RinexData> dataList, long gpsTime) {
        Log.i(TAG, "writeData");
        //Date time and total (Title)
        resetLine();
        Date date = new Date(gpsTime);
        SimpleDateFormat formatYear = new SimpleDateFormat("yy", Locale.US);
        SimpleDateFormat formatMonth = new SimpleDateFormat("M", Locale.US);
        SimpleDateFormat formatDay = new SimpleDateFormat("d", Locale.US);
        SimpleDateFormat formatHour = new SimpleDateFormat("H", Locale.US);
        SimpleDateFormat formatMin = new SimpleDateFormat("m", Locale.US);
        SimpleDateFormat formatSec = new SimpleDateFormat("ss.SSSSSSS", Locale.US);

        String year = formatYear.format(date);
        String month = formatMonth.format(date);
        String day = formatDay.format(date);
        String hour = formatHour.format(date);
        String min = formatMin.format(date);
        String sec = formatSec.format(date);
        String type = "0";
        String total = String.valueOf(dataList.size());

        if (ver == Constants.VER_2_11) { //For Rinex version 2.11
            for (int i = 0; i < year.length(); i++)
                line[2 - i] = year.charAt(year.length() - 1 - i);
            for (int i = 0; i < month.length(); i++)
                line[5 - i] = month.charAt(month.length() - 1 - i);
            for (int i = 0; i < day.length(); i++)
                line[8 - i] = day.charAt(day.length() - 1 - i);
            for (int i = 0; i < hour.length(); i++)
                line[11 - i] = hour.charAt(hour.length() - 1 - i);
            for (int i = 0; i < min.length(); i++)
                line[14 - i] = min.charAt(min.length() - 1 - i);
            for (int i = 0; i < sec.length(); i++)
                line[25 - i] = sec.charAt(sec.length() - 1 - i);
            for (int i = 0; i < type.length(); i++)
                line[28 - i] = type.charAt(type.length() - 1 - i);
            for (int i = 0; i < total.length(); i++)
                line[31 - i] = total.charAt(total.length() - 1 - i);
            for (int i = 0; i < dataList.size(); i++) {
                if (i < 12)
                    for (int j = 0; j < dataList.get(i).getSatellite().length(); j++)
                        line[32 + j + i * 3] = dataList.get(i).getSatellite().charAt(j);
                else
                    break;
            }
            writeLine("");
            if (dataList.size() > 11) { //Satellite total more 12 to new line
                resetLine();
                for (int i = 12; i < dataList.size(); i++)
                    if (i < 23)
                        for (int j = 0; j < dataList.get(i).getSatellite().length(); j++)
                            line[32 + j + ((i - 12) * 3)] = dataList.get(i).getSatellite().charAt(j);
                    else break;
                writeLine("");
            }
            if (dataList.size() > 23) { //Satellite total more 24 to new line
                resetLine();
                for (int i = 24; i < dataList.size(); i++)
                    if (i < 35)
                        for (int j = 0; j < dataList.get(i).getSatellite().length(); j++)
                            line[32 + j + ((i - 24) * 3)] = dataList.get(i).getSatellite().charAt(j);
                    else break;
                writeLine("");
            }

            //Data (content)
            dataList.forEach(data -> {
                resetLine();
                for (int i = 0; i < data.getPseudoRange().length(); i++)
                    line[13 - i] = data.getPseudoRange().charAt(data.getPseudoRange().length() - 1 - i);
                for (int i = 0; i < data.getCarrierPhase().length(); i++)
                    line[29 - i] = data.getCarrierPhase().charAt(data.getCarrierPhase().length() - 1 - i);
                for (int i = 0; i < data.getSignalStrength().length(); i++)
                    line[45 - i] = data.getSignalStrength().charAt(data.getSignalStrength().length() - 1 - i);
                for (int i = 0; i < data.getDoppler().length(); i++)
                    line[60 - i] = data.getDoppler().charAt(data.getDoppler().length() - 1 - i);
                writeLine("");
            });
        } else if (ver == Constants.VER_3_03) { //For Rinex version 3.03
            line[0] = '>';
            for (int i = 0; i < year.length(); i++)
                line[5 - i] = year.charAt(year.length() - 1 - i);
            for (int i = 0; i < month.length(); i++)
                line[8 - i] = month.charAt(month.length() - 1 - i);
            for (int i = 0; i < day.length(); i++)
                line[11 - i] = day.charAt(day.length() - 1 - i);
            for (int i = 0; i < hour.length(); i++)
                line[14 - i] = hour.charAt(hour.length() - 1 - i);
            for (int i = 0; i < min.length(); i++)
                line[17 - i] = min.charAt(min.length() - 1 - i);
            for (int i = 0; i < sec.length(); i++)
                line[28 - i] = sec.charAt(sec.length() - 1 - i);
            for (int i = 0; i < type.length(); i++)
                line[31 - i] = type.charAt(type.length() - 1 - i);
            for (int i = 0; i < total.length(); i++)
                line[34 - i] = total.charAt(total.length() - 1 - i);
            writeLine("");

            //Data (content)
            dataList.forEach(data -> {
                resetLine();
                for (int i = 0; i < data.getSatellite().length(); i++)
                    line[2 - i] = data.getSatellite().charAt(data.getSatellite().length() - 1 - i);
                for (int i = 0; i < data.getPseudoRange().length(); i++)
                    line[16 - i] = data.getPseudoRange().charAt(data.getPseudoRange().length() - 1 - i);
                for (int i = 0; i < data.getCarrierPhase().length(); i++)
                    line[33 - i] = data.getCarrierPhase().charAt(data.getCarrierPhase().length() - 1 - i);
                for (int i = 0; i < data.getDoppler().length(); i++)
                    line[48 - i] = data.getDoppler().charAt(data.getDoppler().length() - 1 - i);
                for (int i = 0; i < data.getSignalStrength().length(); i++)
                    line[64 - i] = data.getSignalStrength().charAt(data.getSignalStrength().length() - 1 - i);
                writeLine("");
            });
        }
    }
}