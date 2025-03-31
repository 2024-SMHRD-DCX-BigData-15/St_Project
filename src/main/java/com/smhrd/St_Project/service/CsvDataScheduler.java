package com.smhrd.St_Project.service;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
//1
@Service
public class CsvDataScheduler {

    @Autowired
    private TankDataService tankDataService;

    private int lastProcessedRow = 0; // ✅ 마지막으로 저장된 행의 번호
    private static final int batchSize = 4; // ✅ 한 번에 저장할 행 개수 (4개)
    private int totalRows = 0; // ✅ CSV 총 행 개수 저장

    /**
     * 🔹 일정 시간마다 실행되는 CSV 데이터 업로드
     */
    @Scheduled(fixedRate = 1000*10) // ✅ 30초(30,000ms)마다 실행 추후 수정 일단은 시간 늘려놓음
    public void loadCsvDataPeriodically() {
        try {
            System.out.println("🔄 CSV 데이터 업로드 시작...");

            CSVReader csvReader = new CSVReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("수질데이터.csv"), StandardCharsets.UTF_8));

            List<String[]> tankDataList = new ArrayList<>();
            String[] nextRecord;
            boolean isFirstRow = true;
            int currentRow = 0;

            // ✅ CSV의 총 행 개수 파악 (첫 실행 시)
            if (totalRows == 0) {
                while (csvReader.readNext() != null) {
                    totalRows++;
                }
                csvReader.close();
                csvReader = new CSVReader(new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("수질데이터.csv"), StandardCharsets.UTF_8));
            }

            while ((nextRecord = csvReader.readNext()) != null) {
                if (isFirstRow) { 
                    isFirstRow = false; // ✅ 첫 번째 행(헤더)은 무시
                    continue;
                }

                currentRow++;

                // ✅ 마지막으로 저장된 행보다 이후 행부터 읽음
                if (currentRow <= lastProcessedRow) {
                    continue;
                }

                tankDataList.add(nextRecord);

                // ✅ 배치 크기(4개)만큼 저장 후 종료
                if (tankDataList.size() >= batchSize) {
                    break;
                }
            }

            // ✅ 데이터베이스에 저장
            tankDataService.saveTankData(tankDataList);

            // ✅ 마지막으로 저장된 행의 번호 업데이트
            lastProcessedRow += tankDataList.size();

            // ✅ CSV의 마지막 행까지 저장한 경우 처음부터 다시 시작
            if (lastProcessedRow >= totalRows) {
                lastProcessedRow = 0; // ✅ 처음부터 다시 시작
                System.out.println("🔄 모든 데이터를 업로드 완료! 처음부터 다시 시작합니다.");
            }

            System.out.println("✅ CSV 데이터가 저장되었습니다! (총 " + tankDataList.size() + "개)");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ CSV 업로드 실패!");
        }
    }
}
