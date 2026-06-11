import { Suspense } from "react";

import PaymentResultPageContent from "@/components/payment/PaymentResultPageContent";

export default function PaymentSuccessPage() {
    return (
        <Suspense fallback={null}>
            <PaymentResultPageContent resultType="success" />
        </Suspense>
    );
}