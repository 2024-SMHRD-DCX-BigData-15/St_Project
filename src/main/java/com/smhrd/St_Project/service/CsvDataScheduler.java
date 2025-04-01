package com.smhrd.St_Project.service;

import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvDataScheduler {

    @Autowired
    private TankDataService tankDataService;

    private int lastProcessedRow = 0; // ✅ 마지막으로 저장된 행 번호
    private static final int batchSize = 4; // ✅ 한 번에 저장할 행 개수
    private int totalRows = 0; // ✅ CSV 총 행 개수 저장

    /**
     * 🔄 일정 시간마다 실행되는 CSV 데이터 업로드
     */
    @Scheduled(fixedRate = 1000*10) // 10초마다 실행
    public void loadCsvDataPeriodically() {
        try {
            System.out.println("🔁 CSV 데이터 업로드 시작...");

            // 🔁 매번 새로운 스트림으로 CSVReader를 생성하여 스트림 초기화 문제 방지
            CSVReader csvReader = new CSVReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("수질데이터.csv"), StandardCharsets.UTF_8));

            List<String[]> tankDataList = new ArrayList<>();
            String[] nextRecord;
            boolean isFirstRow = true;
            int currentRow = 0;

            // ✅ 총 행 수는 한 번만 계산 (초기화 시)
            if (totalRows == 0) {
                while (csvReader.readNext() != null) {
                    totalRows++;
                }
                csvReader.close();
                System.out.println("📦 CSV 총 행 수: " + totalRows);

                // 다시 처음부터 읽기 위해 재생성
                csvReader = new CSVReader(new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("수질데이터.csv"), StandardCharsets.UTF_8));
            }

            // 🔁 다시 읽기 시작
            while ((nextRecord = csvReader.readNext()) != null) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // 헤더 건너뛰기
                }

                currentRow++;

                if (currentRow <= lastProcessedRow) {
                    continue; // 이전에 처리한 행이면 넘어감
                }

                tankDataList.add(nextRecord);

                if (tankDataList.size() >= batchSize) {
                    break; // 4개만 저장
                }
            }

            csvReader.close();

            if (!tankDataList.isEmpty()) {
                // ✅ 데이터 저장
                tankDataService.saveTankData(tankDataList);
                lastProcessedRow += tankDataList.size();

                System.out.println("✅ 저장된 행 수: " + tankDataList.size() + ", 현재 행: " + lastProcessedRow);
            } else {
                System.out.println("⚠️ 저장할 새로운 데이터가 없습니다.");
            }

            // ✅ 마지막까지 다 읽은 경우 처음부터 시작
            if (lastProcessedRow >= totalRows - 1) {
                lastProcessedRow = 0;
                System.out.println("🔄 모든 데이터 저장 완료. 다음 주기부터 다시 시작합니다!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ CSV 업로드 중 오류 발생!");
        }
    }
}
